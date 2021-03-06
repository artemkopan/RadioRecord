package io.shared.store.player

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import io.shared.app.R
import io.shared.core.Logger
import io.shared.core.MainDispatcher
import io.shared.core.Optional
import io.shared.core.toOptional
import io.shared.model.Playlist
import io.shared.model.TrackItem
import io.shared.model.TrackSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

actual class MediaPlayer(
    private val context: Context,
    private val controllerPlayer: PlayerNotificationController
) {

    private val playbackStateMutableFlow = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    actual val playbackStateFlow: StateFlow<PlaybackState> = playbackStateMutableFlow

    private val trackMutableFlow = MutableStateFlow<Optional<TrackItem>>(Optional.empty())
    actual val trackFlow: StateFlow<Optional<TrackItem>> = trackMutableFlow

    private val trackTimeLineMutableFlow = MutableStateFlow<Optional<TimeLine>>(Optional.empty())
    actual val trackTimeLineFlow: StateFlow<Optional<TimeLine>> = trackTimeLineMutableFlow

    private val playerMetaDataMutableFlow =
        MutableStateFlow<Optional<PlayerMetaData>>(Optional.empty())
    actual val playerMetaDataFlow: StateFlow<Optional<PlayerMetaData>> = playerMetaDataMutableFlow

    private val streamMetaDataMutableFlow =
        MutableStateFlow<Optional<StreamMetaData>>(Optional.empty())
    actual val streamMetaDataFlow: StateFlow<Optional<StreamMetaData>> = streamMetaDataMutableFlow

    private val playlistMutableFlow = MutableStateFlow<Optional<Playlist>>(Optional.empty())
    actual val playlistFlow: StateFlow<Optional<Playlist>> = playlistMutableFlow

    private val errorMutableStateFlow = MutableStateFlow<Optional<PlaybackError>>(Optional.empty())
    actual val errorFlow: StateFlow<Optional<PlaybackError>>
        get() = errorMutableStateFlow

    private val playerDispatcher = MainDispatcher
    private val window = Timeline.Window()

    private var _exoPlayer: SimpleExoPlayer? = null
    private val exoPlayer: SimpleExoPlayer
        get() {
            if (_exoPlayer == null) {
                _exoPlayer = initPlayer()
            }
            return _exoPlayer!!
        }

    private val Player.currentTrack: TrackItem?
        get() {
            return currentTag as? TrackItem
        }

    private val playerScope = CoroutineScope(SupervisorJob() + playerDispatcher)
    private var tickerJob: Job? = null

    actual suspend fun prepare(trackItem: TrackItem, playlist: Playlist?, autoPlay: Boolean) =
        withContext(playerDispatcher) {

            val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, "RadioRecord")
            )

            fun source(track: TrackItem): MediaSource = when (val trackSource = track.source) {
                is TrackSource.ProgressiveStream -> {
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .setTag(track)
                        .createMediaSource(trackSource.link.toUri())
                }
                is TrackSource.Progressive -> {
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .setTag(track)
                        .createMediaSource(trackSource.link.toUri())
                }
                is TrackSource.Hls -> {
                    HlsMediaSource.Factory(dataSourceFactory)
                        .setTag(track)
                        .createMediaSource(trackSource.link.toUri())
                }
                else -> throw NotImplementedError()
            }

            val source: MediaSource
            val windowIndex: Int
            if (playlist != null && playlist.tracks.isNotEmpty()) {
                source = ConcatenatingMediaSource(*playlist.tracks.map { source(it) }
                    .toTypedArray())
                windowIndex = playlist.position
            } else {
                source = source(trackItem)
                windowIndex = 0
            }

            startTicker()

            exoPlayer.prepare(source)
            exoPlayer.seekTo(windowIndex, C.TIME_UNSET)
            if (autoPlay) {
                play()
            }
        }

    actual suspend fun release() {
        withContext(playerDispatcher) {
            playerScope.coroutineContext.cancelChildren()
            val exoPlayer = _exoPlayer ?: return@withContext
            _exoPlayer = null
            notificationManger.setPlayer(null)
            exoPlayer.removeListener(eventListener)
            exoPlayer.release()
        }
    }

    actual suspend fun play() = withContext(playerDispatcher) {
        exoPlayer.playWhenReady = true
    }

    actual suspend fun pause() = withContext(playerDispatcher) {
        exoPlayer.playWhenReady = false
    }

    actual suspend fun slip(offset: Duration) = withContext(playerDispatcher) {
        exoPlayer.seekTo(exoPlayer.currentPosition + offset.toLong(DurationUnit.MILLISECONDS))
    }

    actual suspend fun seekTo(position: Duration) = withContext(playerDispatcher) {
        exoPlayer.seekTo(position.toLong(DurationUnit.MILLISECONDS))
    }

    actual suspend fun next() = withContext(playerDispatcher) {
        val player = exoPlayer
        val timeline: Timeline = player.currentTimeline
        if (timeline.isEmpty) {
            return@withContext
        }
        val windowIndex: Int = player.currentWindowIndex
        val nextWindowIndex: Int = player.nextWindowIndex
        if (nextWindowIndex != C.INDEX_UNSET) {
            player.seekTo(nextWindowIndex, C.TIME_UNSET)
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            player.seekTo(windowIndex, C.TIME_UNSET)
        }
    }

    actual suspend fun previous() = withContext(playerDispatcher) {
        val player = exoPlayer
        val timeline: Timeline = player.currentTimeline
        if (timeline.isEmpty) {
            return@withContext
        }
        val windowIndex: Int = player.currentWindowIndex
        timeline.getWindow(windowIndex, window)
        val previousWindowIndex: Int = player.previousWindowIndex
        if (previousWindowIndex != C.INDEX_UNSET && (player.currentPosition <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS || window.isDynamic && !window.isSeekable)
        ) {
            player.seekTo(previousWindowIndex, C.TIME_UNSET)
        } else {
            player.seekTo(windowIndex, 0)
        }
    }

    private fun initPlayer(): SimpleExoPlayer {
        val rendersFactory = AudioRenderersFactory(context)
        rendersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        val player = SimpleExoPlayer.Builder(context, rendersFactory).build()

        player.addAnalyticsListener(AudioEventLogger(TAG))
        player.addAnalyticsListener(metaDataEventListener)
        player.setAudioAttributes(audioAttributes, true)
        player.addListener(eventListener)

        notificationManger.setPlayer(player)
        return player
    }

    private val notificationManger: PlayerNotificationManager by lazy {
        val channelId = "player-channel"
        NotificationUtil.createNotificationChannel(
            context,
            channelId,
            R.string.notification_player_channel_name,
            R.string.notification_player_channel_description,
            NotificationUtil.IMPORTANCE_HIGH
        )
        PlayerNotificationManager(
            context,
            channelId,
            312,
            controllerPlayer,
            controllerPlayer
        ).also {
            it.setPriority(NotificationCompat.PRIORITY_MAX)
            it.setFastForwardIncrementMs(NOTIFICATION_SEEK_MS)
            it.setRewindIncrementMs(NOTIFICATION_SEEK_MS)
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            return when (playbackState) {
                Player.STATE_BUFFERING -> {
                    playbackStateMutableFlow.value = PlaybackState.Buffering
                    errorMutableStateFlow.value = Optional.empty()
                }
                Player.STATE_ENDED -> {
                    playbackStateMutableFlow.value = PlaybackState.Ended
                }
                Player.STATE_READY -> {
                    errorMutableStateFlow.value = Optional.empty()
                    playbackStateMutableFlow.value = if (exoPlayer.playWhenReady) {
                        PlaybackState.Play
                    } else {
                        PlaybackState.Pause
                    }
                }
                Player.STATE_IDLE -> {
                    //ignore this state because it is set after got an error which reset stateFlow
                }
                else -> {
                    Logger.w("Unknown playback state $playbackState", tag = TAG)
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            errorMutableStateFlow.value = Optional.of(
                PlaybackError(
                    trackItem = _exoPlayer?.currentTrack,
                    message = error.message ?: "playback error",
                    cause = error
                )
            )
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            if (reason == Player.TIMELINE_CHANGE_REASON_DYNAMIC) {
                trackChanged()
            }
            updateMetadata()
            updateTimeLine()
        }


        override fun onPositionDiscontinuity(reason: Int) {
            trackChanged()
            updateMetadata()
            updateTimeLine()
        }
    }
    private val metaDataEventListener: AnalyticsListener = object : AnalyticsListener {
        override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            if (metadata.length() > 0) {
                for (i in 0 until metadata.length()) {
                    val entry = metadata[i]
                    if (entry is IcyInfo) {
                        streamMetaDataMutableFlow.value =
                            StreamMetaData(entry.title.orEmpty()).toOptional()
                        break
                    }
                }
            }
        }
    }

    private fun trackChanged() {
        val data = playlistMutableFlow.value.data
        val track = exoPlayer.currentTrack
        if (track == null) {
            trackMutableFlow.value = Optional.empty()
            playlistMutableFlow.value = data?.copy(position = -1).toOptional()
        } else {
            trackMutableFlow.value = track.toOptional()
            playlistMutableFlow.value =
                data?.copy(position = data.tracks.indexOf(track)).toOptional()
        }
    }

    private fun updateMetadata() {
        exoPlayer.let {
            var enableSeeking = false
            var enablePrevious = false
            var enableRewind = false
            var enableFastForward = false
            var enableNext = false

            val timeline: Timeline = it.currentTimeline
            if (!timeline.isEmpty) {
                timeline.getWindow(it.currentWindowIndex, window)
                val isSeekable: Boolean = window.isSeekable
                enableSeeking = isSeekable
                enablePrevious = isSeekable || it.hasPrevious()
                enableRewind = isSeekable
                enableFastForward = isSeekable
                enableNext = window.isDynamic || it.hasNext()
            }

            playerMetaDataMutableFlow.value = PlayerMetaData(
                exoPlayer.audioSessionId,
                enableSeeking,
                enablePrevious,
                enableRewind,
                enableFastForward,
                enableNext
            ).toOptional()
        }
    }

    private fun updateTimeLine() {
        val player = exoPlayer
        val playbackState = player.playbackState

        if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
            return
        }
        val contentDuration = player.contentDuration
        if (contentDuration == C.TIME_UNSET) {
            trackTimeLineMutableFlow.value = Optional.empty()
        } else {
            trackTimeLineMutableFlow.value = TimeLine(
                player.contentPosition.toDuration(DurationUnit.MILLISECONDS),
                player.bufferedPosition.toDuration(DurationUnit.MILLISECONDS),
                contentDuration.toDuration(DurationUnit.MILLISECONDS)
            ).toOptional()
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = ticker(1000L, 0L, playerDispatcher).consumeAsFlow()
            .onEach { updateTimeLine() }
            .launchIn(playerScope)
    }

    private companion object {
        const val TAG = "ExoPlayer"
        private const val MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000
        private const val NOTIFICATION_SEEK_MS = 10_000L
    }

}