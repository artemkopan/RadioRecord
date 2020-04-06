package io.radio.data.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import io.radio.R
import io.radio.shared.base.Logger
import io.radio.shared.base.MainDispatcher
import io.radio.shared.domain.player.*
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
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
                when (it) {
                    is PlayerAction.Preparing -> consumePreparing(it.track)
                    PlayerAction.Release -> consumeRelease()
                    PlayerAction.Play -> consumePlay()
                    PlayerAction.Pause -> consumePause()
                    is PlayerAction.SetPosition -> consumeSetPosition(it.position)
                    is PlayerAction.SeekTo -> consumeSeekTo(it.offset)
                }
            }
            .launchIn(scope)

        scope.launch {
            ticker(1000L, 0L, scope.coroutineContext).consumeEach {
                updateTimeLine()
            }
        }
    }

    override fun destroy() {
        basePlayerController.destroy()
        scope.cancel()
    }

    private suspend fun consumePreparing(track: TrackItem) {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(context, "RadioRecord")
        )

        val source: MediaSource = when (val trackSource = track.source) {
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

        withContext(playerDispatcher) {
            exoPlayer.prepare(source)
        }
    }

    private suspend fun consumeRelease() {
        val exoPlayer = this.exoPlayer
        this._exoPlayer = null
        withContext(playerDispatcher) {
            notificationManger.setPlayer(null)
            exoPlayer.removeListener(eventListener)
            exoPlayer.release()
        }
    }

    private suspend fun consumePlay() {
        withContext(playerDispatcher) { exoPlayer.playWhenReady = true }
    }

    private suspend fun consumePause() {
        withContext(playerDispatcher) { exoPlayer.playWhenReady = false }
    }

    private suspend fun updateTimeLine() {
        _exoPlayer?.let {
            withContext(playerDispatcher) {
                if (it.playbackState != STATE_READY || !it.playWhenReady) {
                    return@withContext
                }
                val contentDuration = it.contentDuration
                if (contentDuration == C.TIME_UNSET) {
                    basePlayerController.postSideEffects(PlayerSideEffect.TrackPositionReset)
                } else {
                    basePlayerController.postSideEffects(
                        PlayerSideEffect.TrackPosition(
                            it.contentPosition.toDuration(DurationUnit.MILLISECONDS),
                            it.bufferedPosition.toDuration(DurationUnit.MILLISECONDS),
                            contentDuration.toDuration(DurationUnit.MILLISECONDS)
                        )
                    )
                }
            }
        }
    }

    private suspend fun consumeSeekTo(offset: Duration) {
        withContext(playerDispatcher) {
            _exoPlayer?.let {
                it.seekTo(it.currentPosition + offset.toLong(DurationUnit.MILLISECONDS))
            }
        }
    }

    private suspend fun consumeSetPosition(position: Duration) {
        withContext(playerDispatcher) { _exoPlayer?.seekTo(position.toLong(DurationUnit.MILLISECONDS)) }
    }

    private val eventListener = object : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                STATE_READY -> {
                    if (playWhenReady) {
                        basePlayerController.postState(PlayerState.PlayTrack)
                        basePlayerController.postSideEffects(
                            PlayerSideEffect.MetaData(PlayerMetaData(exoPlayer.audioSessionId))
                        )
                    } else {
                        basePlayerController.postState(PlayerState.PauseTrack)
                    }
                }
                STATE_ENDED -> basePlayerController.postState(PlayerState.EndedTrack)
                STATE_BUFFERING -> basePlayerController.postState(PlayerState.BufferingTrack)
                STATE_IDLE -> {
                    //no-op
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            basePlayerController.postState(PlayerState.Error(error))
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            scope.launch { updateTimeLine() }
        }

        override fun onPositionDiscontinuity(reason: Int) {
            scope.launch { updateTimeLine() }
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

        player.addAnalyticsListener(AudioEventLogger())
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

    private class AudioEventLogger : EventLogger(null, TAG) {
        override fun logd(msg: String) {
            Logger.d(TAG, msg)
        }

        override fun loge(msg: String, tr: Throwable?) {
            Logger.e(TAG, msg, tr)
        }
    }

    private class AudioRenderersFactory(context: Context) : DefaultRenderersFactory(context) {

        override fun buildVideoRenderers(
            context: Context,
            extensionRendererMode: Int,
            mediaCodecSelector: MediaCodecSelector,
            drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?,
            playClearSamplesWithoutKeys: Boolean,
            enableDecoderFallback: Boolean,
            eventHandler: Handler,
            eventListener: VideoRendererEventListener,
            allowedVideoJoiningTimeMs: Long,
            out: ArrayList<Renderer>
        ) {
            //nothing
        }

        override fun buildCameraMotionRenderers(
            context: Context,
            extensionRendererMode: Int,
            out: ArrayList<Renderer>
        ) {
            //nothing
        }

        override fun buildTextRenderers(
            context: Context,
            output: TextOutput,
            outputLooper: Looper,
            extensionRendererMode: Int,
            out: ArrayList<Renderer>
        ) {
            //noting
        }
    }

    private companion object {
        const val TAG = "ExoPlayer"
    }
}