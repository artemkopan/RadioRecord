package io.shared.store.player

import io.shared.mvi.Reducer
import io.shared.mvi.StateStorage
import io.shared.mvi.StoreFactory
import io.shared.mvi.StoreImpl
import io.shared.store.player.PlayerStore.*
import io.shared.store.player.middleware.*
import kotlinx.coroutines.CoroutineScope


class PlayerStoreFactory(
    private val playerChangeTrackMiddleware: PlayerChangeTrackMiddleware,
    private val playerObserveMetaDataMiddleware: PlayerObserveMetaDataMiddleware,
    private val playerObserveStateMiddleware: PlayerObserveStateMiddleware,
    private val playerObserveErrorMiddleware: PlayerObserveErrorMiddleware,
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
                playerObserveErrorMiddleware,
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