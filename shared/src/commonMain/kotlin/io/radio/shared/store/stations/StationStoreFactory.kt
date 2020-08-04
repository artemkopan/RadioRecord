package io.radio.shared.store.stations

import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.SimpleBootstrapper
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.store.stations.StationStore.*
import kotlinx.coroutines.CoroutineScope

class StationStoreFactory(
    private val loadStationMiddleware: LoadStationMiddleware,
    private val playStationMiddleware: PlayStationMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): StationStore {
        return object : StoreImpl<Action, Result, State>(
            coroutineScope,
            listOf(loadStationMiddleware, playStationMiddleware),
            listOf(SimpleBootstrapper(Action.LoadStations)),
            ReducerImpl,
            State()
        ), StationStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {
        override fun reduce(result: Result, state: State): State = with(result) {
            when (this) {
                Result.Loading -> state.copy(isLoading = true, error = null)
                is Result.StationList -> state.copy(isLoading = false, error = null, data = data)
                is Result.Error -> state.copy(isLoading = false, error = throwable)
                is Result.PlayingStation -> state.copy(playingStation = station)
            }
        }
    }
}