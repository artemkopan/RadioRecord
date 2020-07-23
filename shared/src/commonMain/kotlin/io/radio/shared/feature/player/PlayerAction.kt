package io.radio.shared.feature.player

import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import kotlin.time.Duration


sealed class PlayerAction {

    object PlayPauseClicked : PlayerAction()

    object NextClicked : PlayerAction()

    object PreviousClicked : PlayerAction()

    object SlipForwardClicked : PlayerAction()

    object SlipRewindClicked : PlayerAction()

    data class Prepare(val track: TrackItem, val playlist: Playlist, val autoPlay: Boolean) :
        PlayerAction()

    object Release : PlayerAction()

    data class Seek(val position: Int, val isScrubbing: Boolean) : PlayerAction()

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
            val bufferedPosition: Duration,
            val totalDuration: Duration
        ) : TimeLine()
    }

    data class TrackScrubbing(val timeFormatted: String) : PlayerAction()

}