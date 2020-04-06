package io.radio.data.player

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
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
import io.radio.R
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Logger
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.extensions.JobRunner
import io.radio.shared.domain.player.*
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class AndroidPlayerController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaDescriptionAdapter: NotificationMediaDescriptionAdapter,
    private val notificationListener: NotificationMediaListener,
    private val basePlayerController: BasePlayerController
) : PlayerController by basePlayerController {

    private val playerDispatcher = MainDispatcher
    private val window = Timeline.Window()
    private val timelineJobRunner = JobRunner()

    private var _exoPlayer: SimpleExoPlayer? = null
    private val exoPlayer: SimpleExoPlayer
        get() {
            if (_exoPlayer == null) {
                _exoPlayer = initPlayer()
            }
            return _exoPlayer!!
        }

    private val notificationManger: PlayerNotificationManager by lazy {
        val channelId = "player-channel"
        NotificationUtil.createNotificationChannel(
            context,
            channelId,
            R.string.notification_player_channel_name,
            R.string.notification_player_channel_description,
            NotificationUtil.IMPORTANCE_LOW
        )
        PlayerNotificationManager(
            context,
            channelId,
            312,
            mediaDescriptionAdapter,
            notificationListener
        ).also {
            it.setPriority(NotificationCompat.PRIORITY_MAX)
            it.setFastForwardIncrementMs(PLAYER_SEEK_STEP.toLongMilliseconds())
            it.setRewindIncrementMs(PLAYER_SEEK_STEP.toLongMilliseconds())
        }
    }

    init {
        basePlayerController.playerActionsFlow
            .onEach {
                Logger.d("New player action: $it")
                withContext(playerDispatcher) {
                    when (it) {
                        is PlayerAction.Preparing -> dispatchPreparing(it)
                        PlayerAction.Release -> dispatchRelease()
                        PlayerAction.Play -> dispatchPlay()
                        PlayerAction.Pause -> dispatchPause()
                        PlayerAction.Next -> dispatchNext()
                        PlayerAction.Previous -> dispatchPrevious()
                        is PlayerAction.SetPosition -> dispatchSetPosition(it.position)
                        is PlayerAction.SeekTo -> dispatchSeekTo(it.offset)
                    }
                }
            }
            .launchIn(scope)
    }

    override fun destroy() {
        basePlayerController.destroy()
        scope.cancel()
    }

    private suspend fun dispatchPreparing(action: PlayerAction.Preparing) {
        withContext(IoDispatcher) {
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
            if (action.playlist.tracks.isNotEmpty()) {
                source = ConcatenatingMediaSource(*action.playlist.tracks.map { source(it) }
                    .toTypedArray())
                windowIndex = action.playlist.position
            } else {
                source = source(action.track)
                windowIndex = 0
            }

            withContext(playerDispatcher) {
                exoPlayer.prepare(source)
                exoPlayer.seekTo(windowIndex, C.TIME_UNSET)
            }
        }
    }

    private fun dispatchRelease() {
        val exoPlayer = this.exoPlayer
        this._exoPlayer = null
        notificationManger.setPlayer(null)
        exoPlayer.removeListener(eventListener)
        exoPlayer.release()
    }

    private fun dispatchPlay() {
        exoPlayer.playWhenReady = true
    }

    private fun dispatchPause() {
        exoPlayer.playWhenReady = false
    }

    private fun dispatchNext() {
        val player = exoPlayer
        val timeline: Timeline = player.currentTimeline
        if (timeline.isEmpty) {
            return
        }
        val windowIndex: Int = player.currentWindowIndex
        val nextWindowIndex: Int = player.nextWindowIndex
        if (nextWindowIndex != C.INDEX_UNSET) {
            player.seekTo(nextWindowIndex, C.TIME_UNSET)
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            player.seekTo(windowIndex, C.TIME_UNSET)
        }
    }

    private fun dispatchPrevious() {
        val player = exoPlayer
        val timeline: Timeline = player.currentTimeline
        if (timeline.isEmpty) {
            return
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

    private fun dispatchSeekTo(offset: Duration) {
        exoPlayer.seekTo(exoPlayer.currentPosition + offset.toLong(DurationUnit.MILLISECONDS))

    }

    private fun dispatchSetPosition(position: Duration) {
        exoPlayer.seekTo(position.toLong(DurationUnit.MILLISECONDS))
    }

    private fun updateTimeLine() {
        timelineJobRunner.cancel()
        val player = exoPlayer
        val playbackState = player.playbackState

        fun postUpdate() {
            timelineJobRunner.runAndCancelPrevious {
                scope.launch {
                    delay(1000L)
                    withContext(playerDispatcher) {
                        updateTimeLine()
                    }
                }
            }
        }

        if (player.isPlaying) {
            val contentDuration = player.contentDuration
            if (contentDuration == C.TIME_UNSET) {
                basePlayerController.postSideEffects(PlayerSideEffect.TrackPositionReset)
            } else {
                basePlayerController.postSideEffects(
                    PlayerSideEffect.TrackPosition(
                        player.contentPosition.toDuration(DurationUnit.MILLISECONDS),
                        player.bufferedPosition.toDuration(DurationUnit.MILLISECONDS),
                        contentDuration.toDuration(DurationUnit.MILLISECONDS)
                    )
                )
            }
            postUpdate()
        } else if (playbackState != STATE_ENDED && playbackState != STATE_IDLE) {
            postUpdate()
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

            basePlayerController.postSideEffects(
                PlayerSideEffect.MetaData(
                    PlayerMetaData(
                        exoPlayer.audioSessionId,
                        enableSeeking,
                        enablePrevious,
                        enableRewind,
                        enableFastForward,
                        enableNext
                    )
                )
            )
        }
    }

    private fun convertToState(@Player.State playbackState: Int): PlaybackState? {
        return when (playbackState) {
            STATE_BUFFERING -> PlaybackState.BufferingTrack
            STATE_ENDED -> PlaybackState.EndedTrack
            STATE_READY -> {
                if (exoPlayer.playWhenReady) {
                    PlaybackState.PlayTrack
                } else {
                    PlaybackState.PauseTrack
                }
            }
            STATE_IDLE -> {
                //no-op
                null
            }
            else -> null
        }
    }

    private val eventListener = object : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            convertToState(playbackState)?.let {
                basePlayerController.postState(it)
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            basePlayerController.postState(PlaybackState.Error(error))
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            if (reason == TIMELINE_CHANGE_REASON_DYNAMIC) {
                basePlayerController.postSideEffects(
                    PlayerSideEffect.TrackChanged(
                        exoPlayer.currentTrack(),
                        convertToState(exoPlayer.playbackState)
                    )
                )
            }
            updateMetadata()
            updateTimeLine()
        }

        override fun onPositionDiscontinuity(reason: Int) {
            basePlayerController.postSideEffects(
                PlayerSideEffect.TrackChanged(
                    exoPlayer.currentTrack(),
                    convertToState(exoPlayer.playbackState)
                )
            )
            updateMetadata()
            updateTimeLine()
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


    private val metaDataEventListener: AnalyticsListener = object : AnalyticsListener {
        override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            if (metadata.length() > 0) {
                for (i in 0 until metadata.length()) {
                    val entry = metadata[i]
                    if (entry is IcyInfo) {
                        basePlayerController.postSideEffects(
                            PlayerSideEffect.StreamMetaData(entry.title.orEmpty())
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "ExoPlayer"
        private const val MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000
    }
}