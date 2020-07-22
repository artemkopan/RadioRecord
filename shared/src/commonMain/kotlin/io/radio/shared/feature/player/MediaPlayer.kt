package io.radio.shared.feature.player

import io.radio.shared.base.Optional
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

expect class MediaPlayer {

    val trackFlow: StateFlow<Optional<TrackItem>>
    val trackStateFlow: StateFlow<MediaState>
    val trackTimeLineFlow: StateFlow<Optional<TimeLine>>
    val playerMetaDataFlow: StateFlow<Optional<PlayerMetaData>>
    val streamMetaDataFlow: StateFlow<Optional<StreamMetaData>>
    val playlistFlow: StateFlow<Optional<Playlist>>

    suspend fun prepare(trackItem: TrackItem, playlist: Playlist, autoPlay: Boolean)

    suspend fun release()

    suspend fun play()

    suspend fun pause()

    suspend fun seekTo(position: Duration)

    suspend fun next()

    suspend fun previous()

}

data class TimeLine(
    val currentPosition: Duration,
    val bufferedPosition: Duration,
    val totalDuration: Duration
)


sealed class MediaState {
    object Idle : MediaState()
    object Buffering : MediaState()
    object Play : MediaState()
    object Pause : MediaState()
    object Ended : MediaState()
    class Error(val throwable: Throwable) : MediaState()
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