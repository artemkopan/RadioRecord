package io.radio.data.player

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import io.radio.shared.base.Logger
import io.radio.shared.base.extensions.asCoroutineDispatcher
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.player.*
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.model.TrackSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class AndroidPlayerController(
    trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
    trackFormatter: TrackFormatter,
    private val context: Context,
    //initialize in constructor for using kotlin interfaces delegate
    private val handlerThread: HandlerThread = HandlerThread("ExoPlayerThread").also { it.start() },
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + handlerThread.asCoroutineDispatcher("ExoPlayer")
    ),
    private val basePlayerController: BasePlayerController = BasePlayerController(
        scope,
        trackMediaInfoCreatorUseCase,
        trackFormatter
    )
) : PlayerController by basePlayerController {

    private var _exoPlayer: SimpleExoPlayer? = null
    private val exoPlayer: SimpleExoPlayer
        get() {
            if (_exoPlayer == null) {
                _exoPlayer = initPlayer()
            }
            return _exoPlayer!!
        }

    init {
        basePlayerController.playerActionsFlow
            .onEach {
                Logger.d("New player action: $it")
                when (it) {
                    is PlayerAction.Preparing -> consumePreparing(it.source)
                    PlayerAction.Release -> consumeRelease()
                    PlayerAction.Play -> consumePlay()
                    PlayerAction.Pause -> consumePause()
                    is PlayerAction.SetPosition -> consumeSetPosition(it.positionMs)
                    is PlayerAction.SeekTo -> consumeSeekTo(it.offsetMs)
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

    private fun consumePreparing(trackSource: TrackSource) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "RadioRecord")
        )

        val source: MediaSource = when (trackSource) {
            is TrackSource.Progressive -> {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(trackSource.link.toUri())
            }
            is TrackSource.Hls -> TODO()
            else -> throw NotImplementedError()
        }

        exoPlayer.prepare(source)
    }

    private fun consumeRelease() {
        val exoPlayer = this.exoPlayer
        this._exoPlayer = null
        exoPlayer.removeListener(eventListener)
        exoPlayer.release()
    }

    private fun consumePlay() {
        exoPlayer.playWhenReady = true
    }

    private fun consumePause() {
        exoPlayer.playWhenReady = false
    }

    private fun updateTimeLine() {
        _exoPlayer?.let {
            if (it.playbackState != STATE_READY) {
                return
            }
            basePlayerController.postSideEffects(
                PlayerSideEffect.TrackPosition(
                    it.contentPosition.toDuration(DurationUnit.MILLISECONDS),
                    it.bufferedPosition.toDuration(DurationUnit.MILLISECONDS),
                    it.contentDuration.toDuration(DurationUnit.MILLISECONDS)
                )
            )
        }
    }

    private fun consumeSeekTo(offsetMs: Long) {
        _exoPlayer?.let {
            it.seekTo(it.currentPosition + offsetMs)
        }
    }

    private fun consumeSetPosition(position: Long) {
        _exoPlayer?.seekTo(position)
    }

    private val eventListener = object : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Logger.d("onPlayerStateChanged() called with: playWhenReady = $playWhenReady, playbackState = $playbackState")
            when (playbackState) {
                STATE_READY,
                STATE_ENDED -> {
                    if (playWhenReady) {
                        basePlayerController.postState(PlayerState.PlayTrack)
                        basePlayerController.postSideEffects(
                            PlayerSideEffect.MetaData(PlayerMetaData(exoPlayer.audioSessionId))
                        )
                    } else {
                        basePlayerController.postState(PlayerState.PauseTrack)
                    }
                }
                STATE_BUFFERING -> {
                    basePlayerController.postState(PlayerState.BufferingTrack)
                }
                STATE_IDLE -> {
                    //no-op
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Logger.e("onPlayerError() called with: error = $error", error)
            basePlayerController.postState(PlayerState.Error(error))
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Logger.d("onTimelineChanged() called with: timeline = $timeline, reason = $reason")
            updateTimeLine()
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Logger.d("onPositionDiscontinuity() called with: reason = $reason")
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

        val player = SimpleExoPlayer.Builder(context, rendersFactory)
            .setLooper(handlerThread.looper)
            .build()

        player.setAudioAttributes(audioAttributes, true)
        player.addListener(eventListener)
        return player
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
}