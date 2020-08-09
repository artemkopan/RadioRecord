package io.shared.store.player

import io.shared.core.Persistable
import io.shared.model.TrackItem
import io.shared.mvi.Store
import io.shared.store.player.PlayerStore.*
import kotlin.time.Duration

interface PlayerStore : Store<Action, Result, State> {

    sealed class Action {

        data class Prepare(
            val track: TrackItem,
            val tracks: List<TrackItem>,
            val autoPlay: Boolean,
            val forcePrepareIfTrackPrepared: Boolean = false,
            val switchPlaybackIfTrackPrepared:Boolean = false
        ) :  Action()

        object PlayNext : Action()

        object PlayPrevious : Action()

        object SwitchPlayback : Action()

        data class FindPosition(val position: Duration, val isScrubbing: Boolean) : Action()

        object SlipForward : Action()

        object SlipRewind : Action()

    }

    sealed class Result {

        data class PlaylistAvailability(
            val availableSeeking: Boolean,
            val availablePrevious: Boolean,
            val availableRewind: Boolean,
            val availableFastForward: Boolean,
            val availableNext: Boolean
        ) : Result()

        data class TrackChanged(val track: TrackItem?) : Result()

        data class PlaybackError(val throwable: Throwable) : Result()

        object PlaybackBuffering : Result()

        object PlaybackIdle : Result()

        object PlaybackEnded : Result()

        object PlaybackPlay : Result()

        object PlaybackPause : Result()

        data class PlaybackPostRewind(
            val forwardDuration: Duration?,
            val rewindDuration: Duration?
        ) : Result()

        sealed class TimeLine : Result() {
            object None : TimeLine()
            data class Changed(
                val currentDuration: Duration,
                val totalDuration: Duration
            ) : TimeLine()
        }

        data class TrackScrubbing(val position: Duration?) : Result()

    }

    data class State(
        val isPreparing: Boolean = false,
        val isPlaying: Boolean = false,
        val isNextAvailable: Boolean = false,
        val isPreviousAvailable: Boolean = false,
        val isSeekAvailable: Boolean = false,
        val isFastForwardAvailable: Boolean = false,
        val isRewindAvailable: Boolean = false,
        val track: TrackItem? = null,
        val currentDuration: Duration? = null,
        val totalDuration: Duration? = null,
        val scrubbingPosition: Duration? = null,
        val forwardDuration: Duration? = null,
        val rewindDuration: Duration? = null,
        val error: Throwable? = null
    ) : Persistable

}