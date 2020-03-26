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
import io.radio.shared.domain.player.BasePlayerController
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.model.TrackSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*


class AndroidPlayerController(
    private val context: Context,
    trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase
) : BasePlayerController(trackMediaInfoCreatorUseCase), PlayerController {

    private val handlerThread by lazy {
        HandlerThread("ExoPlayerThread").also { it.start() }
    }
    private val handler = Handler(handlerThread.looper)

    private var _exoPlayer: ExoPlayer? = null
    private val exoPlayer: ExoPlayer
        get() {
            if (_exoPlayer == null) {
                _exoPlayer = initPlayer()
            }
            return _exoPlayer!!
        }

    override val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + handler.asCoroutineDispatcher("ExoPlayer"))

    init {
        playerStates.asFlow()
            .onEach {
                when (it) {
                    is PlayerState.Preparing -> consumePreparing(it.source)
                    PlayerState.Release -> consumeRelease()
                    PlayerState.Play -> consumePlay()
                    PlayerState.Pause -> consumePause()
                }
            }
            .launchIn(scope)
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

    private val eventListener = object : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Logger.d("onPlayerStateChanged() called with: playWhenReady = $playWhenReady, playbackState = $playbackState")
            when (playbackState) {
                STATE_READY,
                STATE_ENDED -> {
                    if (playWhenReady) {
                        sendPlayTrack()
                    } else {
                        sendPauseTrack()
                    }
                }
                STATE_BUFFERING -> {
                    sendBufferingTrack()
                }
                STATE_IDLE -> {
                    //no-op
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Logger.e("onPlayerError() called with: error = $error", error)
            sendError(error)
        }
    }

    private fun initPlayer(): SimpleExoPlayer {
        val rendersFactory =
            AudioRenderersFactory(
                context
            )
        rendersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        val player = SimpleExoPlayer.Builder(context).build()

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