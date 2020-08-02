package io.radio.shared.store.stations

import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.store.stations.StationStore.*
import kotlinx.coroutines.CoroutineScope

class StationStoreFactory(
    private val loadStationMiddleware: LoadStationMiddleware,
    private val loadStationBootstrapper: LoadStationBootstrapper,
    private val playStationMiddleware: PlayStationMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): StationStore {
        return object : StoreImpl<Action, Result, State>(
            coroutineScope,
            listOf(loadStationMiddleware, playStationMiddleware),
            listOf(loadStationBootstrapper),
            ReducerImpl,
            State()
        ), StationStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {
        override fun reduce(result: Result, state: State): State = with(result) {
            when (this) {
                Result.Loading -> state.copy(isLoading = true, error = null)
                is Result.Success -> state.copy(isLoading = false, error = null, data = data)
                is Result.Error -> state.copy(isLoading = false, error = throwable)
            }
        }
    }
}