package io.radio.shared.model

import kotlin.time.Duration

data class TrackMediaInfo(
    val track: TrackItem,
    val state: TrackMediaState,
    val durationFormatted: String
)

sealed class TrackMediaState {

    object None : TrackMediaState()
    object Preparing : TrackMediaState()
    object Buffering : TrackMediaState()
    object Play : TrackMediaState()
    object Pause : TrackMediaState()
    class Error(val throwable: Throwable) : TrackMediaState()

}

data class TrackMediaTimeLine(
    val currentPosition: Duration,
    val bufferedPosition: Duration,
    val totalDuration: Duration,
    val currentDurationFormatted: String,
    val totalDurationFormatted: String
)

@Suppress("NOTHING_TO_INLINE")
inline fun TrackMediaState.isPlayOrPause() =
    this is TrackMediaState.Play || this is TrackMediaState.Pause