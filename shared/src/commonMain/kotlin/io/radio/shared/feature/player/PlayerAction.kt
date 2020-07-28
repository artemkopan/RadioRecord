package io.radio.shared.feature.player

import kotlin.time.Duration


sealed class PlayerAction {

    object PlayPauseIntent : PlayerAction()
    object PlayNextIntent : PlayerAction()
    object PlayPreviousIntent : PlayerAction()
    object SlipForwardIntent : PlayerAction()
    object SlipRewindIntent : PlayerAction()

    data class FindPositionIntent(val position: Int, val isScrubbing: Boolean) : PlayerAction()

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

    data class PlaybackError(val throwable: Throwable) : PlayerAction()

    object PlaybackBuffering : PlayerAction()

    object PlaybackIdle : PlayerAction()

    object PlaybackEnded : PlayerAction()

    object PlaybackPlay : PlayerAction()

    object PlaybackPause : PlayerAction()

    data class PlaybackPostForward(val stepDurationFormatted: String) : PlayerAction()

    data class PlaybackPostRewind(val stepDurationFormatted: String) : PlayerAction()

    sealed class TimeLine : PlayerAction() {
        object None : TimeLine()
        data class Changed(
            val currentPosition: Duration,
            val currentPositionFormatted: String,
            val totalDuration: Duration,
            val totalDurationFormatted: String
        ) : TimeLine()
    }

    data class TrackScrubbing(val timeFormatted: String) : PlayerAction()

}