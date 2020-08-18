package io.shared.store.player

import c.observer.ObserverProtocol
import io.shared.core.Logger
import io.shared.core.MainDispatcher
import io.shared.core.Optional
import io.shared.core.extensions.CoroutineExceptionHandler
import io.shared.core.toOptional
import io.shared.model.Playlist
import io.shared.model.TrackItem
import kotlinx.cinterop.COpaquePointer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSURL
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.MediaPlayer.*
import platform.darwin.NSObject
import kotlin.time.Duration
import kotlin.time.DurationUnit

actual class MediaPlayer {

    private val coroutineContext = SupervisorJob() + MainDispatcher + CoroutineExceptionHandler {
        errorMutableStateFlow.value = PlaybackError(null, "Internal error", it).toOptional()
    }
    private val coroutineScope = CoroutineScope(coroutineContext)

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

    private var player: AVPlayer? = null

    init {
        setupAudioSession()
        combine(listOf(trackMutableFlow, playbackStateMutableFlow)) {
            updateNotificationView()
        }.launchIn(coroutineScope)
    }

    actual suspend fun prepare(
        trackItem: TrackItem,
        playlist: Playlist?,
        autoPlay: Boolean
    ) = withContext(coroutineContext) {
        Logger.d("Prepare track: $trackItem", tag = tag)
        release()

        setupMediaPlayerNotificationCommands()

        trackMutableFlow.value = trackItem.toOptional()

        player =
            AVPlayer.playerWithURL(NSURL(string = trackItem.source.getLinkFromSource())).apply {
                addObserver(statusObserver, statusKey, NSKeyValueObservingOptionNew, null)
                addObserver(
                    timeControlObserver, timeControlKey, NSKeyValueObservingOptionNew, null
                )
                if (autoPlay) {
                    play()
                }
            }
    }

    actual suspend fun release() {
        withContext(coroutineContext) {
            player?.apply {
                pause()
                removeObserver(observer = statusObserver, forKeyPath = statusKey, context = null)
                removeObserver(
                    observer = timeControlObserver,
                    forKeyPath = timeControlKey,
                    context = null
                )
            }
            player = null
        }
    }

    actual suspend fun play() {
        withContext(coroutineContext) {
            player?.play()
        }
    }

    actual suspend fun pause() {
        withContext(coroutineContext) {
            player?.pause()
        }
    }

    actual suspend fun slip(offset: Duration) {
        withContext(coroutineContext) {
            player?.apply {
                val newTime =
                    (CMTimeGetSeconds(currentTime()) - offset.inSeconds).coerceAtLeast(0.0)
                seekToTime(time = CMTimeMake(newTime.toLong(), 1000))
            }
        }
    }

    actual suspend fun seekTo(position: Duration) {
        withContext(coroutineContext) {
            player?.apply {
                seekToTime(time = CMTimeMake(position.toLong(DurationUnit.SECONDS), 1000))
            }
        }
    }

    actual suspend fun next() {

    }

    actual suspend fun previous() {

    }

    //https://youtrack.jetbrains.com/issue/KT-40975
//Ide can't find instance because of using common target for iosX64 and iosArm64
    private val timeControlObserver = object : NSObject(), ObserverProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?,
            ofObject: Any?,
            change: Map<Any?, *>?,
            context: COpaquePointer?
        ) {
            val player = player

            if (keyPath != timeControlKey || player == null) {
                return
            }

            val timeControlStatus = player.timeControlStatus
            Logger.d("TimeControlStatus: $timeControlStatus", tag = tag)
            when (timeControlStatus) {
                AVPlayerTimeControlStatusPlaying -> {
                    playbackStateMutableFlow.value = PlaybackState.Play
                }
                AVPlayerTimeControlStatusPaused -> {
                    playbackStateMutableFlow.value = PlaybackState.Pause
                }
                AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> {
                    playbackStateMutableFlow.value = PlaybackState.Buffering
                }
            }
        }
    }

    private val statusObserver = object : NSObject(), ObserverProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?,
            ofObject: Any?,
            change: Map<Any?, *>?,
            context: COpaquePointer?
        ) {
            val player = player
            if (keyPath != statusKey || player == null) return

            val error = player.error
            val status = player.status
            val reasonForWaitingToPlay = player.reasonForWaitingToPlay

            Logger.d("Player status $status", tag = tag)
            Logger.w("Error status $error", tag = tag)
            Logger.d("Reason for waiting for play: $reasonForWaitingToPlay", tag = tag)

            errorMutableStateFlow.value = error?.let {
                PlaybackError(
                    trackItem = trackMutableFlow.value.data,
                    message = it.description ?: "Payback error",
                    cause = Error(it.debugDescription)
                )
            }.toOptional()

            when (reasonForWaitingToPlay) {
                AVPlayerWaitingWithNoItemToPlayReason -> {
                    playbackStateMutableFlow.value = PlaybackState.Idle
                }
            }
        }
    }

    private fun setupMediaPlayerNotificationCommands() {
        val sharedCommandCenter = MPRemoteCommandCenter.sharedCommandCenter()
        sharedCommandCenter.playCommand.addTargetWithHandler {
            val player = player
            if (player != null) {
                coroutineScope.launch { play() }
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
        sharedCommandCenter.pauseCommand.addTargetWithHandler {
            val player = player
            if (player != null) {
                coroutineScope.launch { pause() }
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
        sharedCommandCenter.previousTrackCommand.addTargetWithHandler {
            val player = player
            if (player != null) {
                coroutineScope.launch { previous() }
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
        sharedCommandCenter.nextTrackCommand.addTargetWithHandler {
            val player = player
            if (player != null) {
                coroutineScope.launch { next() }
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
    }

    private fun updateNotificationView() {
        val track = trackMutableFlow.value.data ?: return
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = mapOf(
            MPMediaItemPropertyTitle to track.title,
            MPNowPlayingInfoPropertyPlaybackRate to (player?.rate ?: 0f)
        )
    }

    private fun setupAudioSession() {
        try {
            AVAudioSession.sharedInstance()
                .setCategory(AVAudioSessionCategoryPlayback, null)
            AVAudioSession.sharedInstance().setActive(true, null)
        } catch (throwable: Throwable) {
            Logger.e("Error while set up audio session", throwable, tag)
        }
    }

    private companion object {
        const val tag = "MediaPlayer"
        const val timeControlKey = "timeControlStatus"
        const val statusKey = "status"
    }

}