package io.radio.shared.store.stations

import io.radio.shared.base.mvi.Bootstrapper
import io.radio.shared.store.stations.StationStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class LoadStationBootstrapper : Bootstrapper<Action, Result, State> {
    override fun accept(
        actions: Flow<Action>,
        results: Flow<Result>,
        stateFlow: StateFlow<State>
    ): Flow<Action> {
        return flowOf(Action.LoadStations)
    }
}