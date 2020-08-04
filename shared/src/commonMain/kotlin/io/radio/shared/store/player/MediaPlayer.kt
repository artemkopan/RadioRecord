package io.radio.shared.store.player

import io.radio.shared.base.Optional
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

expect class MediaPlayer {

    val playbackStateFlow: StateFlow<PlaybackState>
    val trackFlow: StateFlow<Optional<TrackItem>>
    val trackTimeLineFlow: StateFlow<Optional<TimeLine>>
    val playerMetaDataFlow: StateFlow<Optional<PlayerMetaData>>
    val streamMetaDataFlow: StateFlow<Optional<StreamMetaData>>
    val playlistFlow: StateFlow<Optional<Playlist>>

    suspend fun prepare(trackItem: TrackItem, playlist: Playlist?, autoPlay: Boolean)

    suspend fun release()

    suspend fun play()

    suspend fun pause()

    suspend fun slip(offset: Duration)

    suspend fun seekTo(position: Duration)

    suspend fun next()

    suspend fun previous()

}

data class TimeLine(
    val currentPosition: Duration,
    val bufferedPosition: Duration,
    val totalDuration: Duration
)

sealed class PlaybackState {
    object Idle : PlaybackState()
    object Buffering : PlaybackState()
    object Play : PlaybackState()
    object Pause : PlaybackState()
    object Ended : PlaybackState()
    class Error(val throwable: Throwable) : PlaybackState()

    fun isPlayOrPause() = this is Play || this is Pause
}

data class PlayerMetaData(
    val sessionId: Int,
    val availableSeeking: Boolean,
    val availablePrevious: Boolean,
    val availableRewind: Boolean,
    val availableFastForward: Boolean,
    val availableNext: Boolean
)

data class StreamMetaData(val title: String)

