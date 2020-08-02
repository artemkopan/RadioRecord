package io.radio.shared.store.player

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.Store
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.model.TrackItem
import io.radio.shared.store.player.PlayerStore.*
import io.radio.shared.store.player.middleware.*
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration


interface PlayerStore : Store<Action, Result, State> {

    sealed class Action {

        data class Prepare(
            val track: TrackItem,
            val tracks: List<TrackItem>,
            val autoPlay: Boolean
        ) :
            Action()

        data class PrepareOrSwitchPlayPause(
            val track: TrackItem,
            val tracks: List<TrackItem>,
            val autoPlay: Boolean
        ) : Action()

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

class PlayerStoreFactory(
    private val playerChangeTrackMiddleware: PlayerChangeTrackMiddleware,
    private val playerObserveMetaDataMiddleware: PlayerObserveMetaDataMiddleware,
    private val playerObserveStateMiddleware: PlayerObserveStateMiddleware,
    private val playerObserveTimelineMiddleware: PlayerObserveTimelineMiddleware,
    private val playerObserveTrackMiddleware: PlayerObserveTrackMiddleware,
    private val playerPlayPauseMiddleware: PlayerPlayPauseMiddleware,
    private val playerSeekMiddleware: PlayerSeekMiddleware,
    private val playerSlipMiddleware: PlayerSlipMiddleware,
    private val playerPrepareMiddleware: PlayerPrepareMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(coroutineScope: CoroutineScope, stateStorage: StateStorage): PlayerStore {
        return object : StoreImpl<Action, Result, State>(
            coroutineScope = coroutineScope,
            middlewareList = listOf(
                playerChangeTrackMiddleware,
                playerObserveMetaDataMiddleware,
                playerObserveStateMiddleware,
                playerObserveTimelineMiddleware,
                playerObserveTrackMiddleware,
                playerPlayPauseMiddleware,
                playerSeekMiddleware,
                playerSlipMiddleware,
                playerPrepareMiddleware
            ),
            bootstrapperList = emptyList(),
            reducer = ReducerImpl,
            initialState = State()
        ), PlayerStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {

        override fun reduce(result: Result, state: State): State = with(result) {
            return when (this) {
                is Result.PlaylistAvailability -> {
                    state.copy(
                        isNextAvailable = availableNext,
                        isPreviousAvailable = availablePrevious,
                        isSeekAvailable = availableSeeking,
                        isFastForwardAvailable = availableFastForward,
                        isRewindAvailable = availableRewind
                    )
                }
                is Result.TrackChanged -> {
                    state.copy(track = track, error = null)
                }
                is Result.PlaybackError -> {
                    state.copy(error = throwable)
                }
                Result.PlaybackBuffering -> {
                    state.copy(isPreparing = true)
                }
                Result.PlaybackIdle -> {
                    state.copy(isPreparing = false, isPlaying = false)
                }
                Result.PlaybackEnded -> {
                    state.copy(isPreparing = false, isPlaying = false)
                }
                Result.PlaybackPlay -> {
                    state.copy(isPreparing = false, isPlaying = true)
                }
                Result.PlaybackPause -> {
                    state.copy(isPreparing = false, isPlaying = false)
                }
                is Result.PlaybackPostRewind -> {
                    state.copy(
                        forwardDuration = forwardDuration,
                        rewindDuration = rewindDuration
                    )
                }
                Result.TimeLine.None -> {
                    state.copy(
                        currentDuration = null,
                        totalDuration = null
                    )
                }
                is Result.TimeLine.Changed -> {
                    state.copy(
                        currentDuration = currentDuration,
                        totalDuration = totalDuration
                    )
                }
                is Result.TrackScrubbing -> {
                    state.copy(scrubbingPosition = position)
                }
            }
        }

    }
}