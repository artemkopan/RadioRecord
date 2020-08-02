package io.radio.shared.store.stations

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.stations.StationStore.*
import kotlinx.coroutines.flow.*

class LoadStationMiddleware(
    private val radioRepository: RadioRepository
) : Middleware<Action, Result, State> {

    override fun accept(actions: Flow<Action>, state: StateFlow<State>): Flow<Result> {
        return actions.transform {
            if (it is Action.LoadStations) {
                emit(Result.Loading)
                emit(Result.Success(radioRepository.getStations()))
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.Error(cause)); true }
    }

}