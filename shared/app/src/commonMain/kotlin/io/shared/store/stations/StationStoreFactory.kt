package io.shared.store.stations

import io.shared.mvi.*
import io.shared.store.stations.StationStore.*
import kotlinx.coroutines.CoroutineScope

class StationStoreFactory(
    private val loadStationMiddleware: LoadStationMiddleware,
    private val playStationMiddleware: PlayStationMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        tag: String,
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): StationStore {
        return object : StoreImpl<Action, Result, State>(
            tag = tag,
            coroutineScope = coroutineScope,
            middlewareList = listOf(loadStationMiddleware, playStationMiddleware),
            bootstrapperList = listOf(SimpleBootstrapper(Action.LoadStations)),
            reducer = ReducerImpl,
            initialState = stateStorage.getOrDefault("station-store_$tag") { State() }
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