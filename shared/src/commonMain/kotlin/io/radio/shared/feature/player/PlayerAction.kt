package io.radio.shared.feature.player

import kotlin.time.Duration


sealed class PlayerAction {

    object NextClicked : PlayerAction()

    object PreviousClicked : PlayerAction()

    object ForwardClicked : PlayerAction()

    object RewindClicked : PlayerAction()

    data class PlaylistAvailability(
        val availableSeeking: Boolean,
        val availablePrevious: Boolean,
        val availableRewind: Boolean,
        val availableFastForward: Boolean,
        val availableNext: Boolean
    ) : PlayerAction()

    data class TrackChanged(
        val logo: String,
        val title: String,
        val subTitle: String
    ) : PlayerAction()

    data class StreamDataUpdated(val title: String) : PlayerAction()

    data class PlaybackError(val throwable: Throwable) : PlayerAction()

    object PlaybackBuffering : PlayerAction()

    object PlaybackIdle : PlayerAction()

    object PlaybackEnded : PlayerAction()

    object PlaybackPlay : PlayerAction()

    object PlaybackPause : PlayerAction()

    sealed class TimeLine : PlayerAction() {
        object None : TimeLine()
        data class Changed(
            val position: Duration,
            val isScrubbing: Duration,
            val totalDuration: Duration
        ) : TimeLine()
    }

}