package io.shared.store.player

import c.observer.ObserverProtocol
import io.shared.core.Optional
import io.shared.model.Playlist
import io.shared.model.TrackItem
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.*
import platform.darwin.NSObject
import kotlin.time.Duration
import kotlin.time.DurationUnit

actual class MediaPlayer {

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

    actual suspend fun prepare(
        trackItem: TrackItem,
        playlist: Playlist?,
        autoPlay: Boolean
    ) {
        player = AVPlayer.playerWithURL(NSURL(string = trackItem.source.getLinkFromSource())).apply {
            addObserver(
                timeControlObserver,
                timeControlKey,
                NSKeyValueObservingOptionNew,
                null
            )
        }
    }


    actual suspend fun release() {
        player?.apply {
            pause()
            removeObserver(timeControlObserver, timeControlKey)
        }
        player = null
    }

    actual suspend fun play() {
        player?.play()
    }

    actual suspend fun pause() {
        player?.pause()
    }

    actual suspend fun slip(offset: Duration) {
        player?.apply {
            val newTime = (CMTimeGetSeconds(currentTime()) - offset.inSeconds).coerceAtLeast(0.0)
            seekToTime(time = CMTimeMake(newTime.toLong(), 1000))
        }
    }

    actual suspend fun seekTo(position: Duration) {
        player?.apply {
            seekToTime(time = CMTimeMake(position.toLong(DurationUnit.SECONDS), 1000))
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
//            if (keyPath != timeControlKey) {
//                return
//            }
//
//            val newValue = change?.get(NSKeyValueChangeOldKey) as? Int
//            val oldValue = change?.get(NSKeyValueChangeOldKey) as? Int
//
//            val newStatus = newValue?.objcPtr()?.let { AVPlayerTimeControlStatusVar(it).value }
//            val oldStatus = oldValue?.objcPtr()?.let { AVPlayerTimeControlStatusVar(it).value }
//
//            when (newStatus) {
//                oldStatus -> return
//                AVPlayerTimeControlStatusPlaying -> {
//                    playbackStateMutableFlow.value = PlaybackState.Play
//                }
//                AVPlayerTimeControlStatusPaused -> {
//                    playbackStateMutableFlow.value = PlaybackState.Pause
//                }
//                AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate -> {
//                    playbackStateMutableFlow.value = PlaybackState.Buffering
//                }
//            }
        }
    }

    private companion object {
        const val timeControlKey = "timeControlStatus"
    }

}