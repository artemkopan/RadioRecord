package io.radio.shared.model

data class TrackMediaInfo(
    val track: TrackItem,
    val state: TrackMediaState,
    val playTimeFormatted: String
)


sealed class TrackMediaState {

    object None : TrackMediaState()
    object Preparing : TrackMediaState()
    object Buffering : TrackMediaState()
    object Play : TrackMediaState()
    object Pause : TrackMediaState()
    class Error(val throwable: Throwable) : TrackMediaState()

}

@Suppress("NOTHING_TO_INLINE")
inline fun TrackMediaState.isPlayOrPause() =
    this is TrackMediaState.Play || this is TrackMediaState.Pause