package io.radio.shared.feature.player

import io.radio.shared.base.mvi.Store
import io.radio.shared.base.mvi.StoreBuilder
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.StoreViewModel

class PlayerViewModel(
    private val playerReducer: PlayerReducer,
    private val stateStorage: StateStorage
) : StoreViewModel<PlayerAction, PlayerState, Nothing>() {

    override val store: Store<PlayerAction, PlayerState, Nothing>
        get() = StoreBuilder<PlayerAction, PlayerState, Nothing>()
            .enableLogging()
            .enablePersistState("player", stateStorage)
            .reducer(playerReducer)
            .initialState(PlayerState())
            .build()

//    override val reducer: Reducer<PlayerViewAction, PlayerViewState, PlayerViewSideEffect>
//        get() = Reducer(PlayerViewState()) { action, state, _ ->
//            return@Reducer when (action) {
//                PlayerViewAction.PlayPauseClicked -> {
//                    trackMediaInfoProcessUseCase.execute(TrackMediaInfoProcessParams(trackFlow.first()))
//                    state
//                }
//                is PlayerViewAction.PositionChanged -> {
//                    trackUpdatePositionUseCase.execute(
//                        action.position,
//                        action.isScrubbing
//                    ).let { state.copy(currentDuration = it) }
//                }
//                PlayerViewAction.NextClicked -> {
//                    playerController.next()
//                    state
//                }
//                PlayerViewAction.PreviousClicked -> {
//                    playerController.previous()
//                    state
//                }
//                PlayerViewAction.ForwardClicked -> {
//                    seek(true)
//                    state
//                }
//                PlayerViewAction.RewindClicked -> {
//                    seek(false)
//                    state
//                }
//                is PlayerViewAction.Buffering -> {
//
//                }
//            }
//        }
//
//    override fun proceed(action: PlayerViewAction) {
//        scope.launch(CoroutineExceptionHandler { reducer. }) {
//            reducer.proceed(action)
//        }
//    }
//
//    private suspend fun seek(isForward: Boolean) {
//        seekJobRunner.runAndCancelPrevious {
//            //todo error handler
//            scope.launch(context = CoroutineExceptionHandler { throwable -> }) {
//                if (enableSeeking) {
//                    val duration = trackSeekUseCase.execute(isForward)
//                    if (duration.isNotEmpty()) {
//                        val formatted = dateProvider.formatSec(duration.getOrThrow())
//                        seekResultChannel.send(
//                            (if (isForward) {
//                                PlayerViewModel.SeekResult.Forward(formatted)
//                            } else {
//                                PlayerViewModel.SeekResult.Rewind(formatted)
//                            }).toOptional()
//                        )
//                        delay(PlayerViewModel.SEEK_DELAY)
//                        seekResultChannel.send(Optional.empty())
//                    }
//                }
//            }
//        }
//    }

}