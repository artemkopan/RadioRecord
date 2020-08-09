package io.shared.store.stations

import io.shared.core.IoDispatcher
import io.shared.mvi.Middleware
import io.shared.repo.RadioRepository
import io.shared.store.stations.StationStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform

class LoadStationMiddleware(
    private val radioRepository: RadioRepository
) : Middleware<Action, Result, State> {

    override fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result> {
        return actionFlow.transform {
            if (it is Action.LoadStations) {
                emit(Result.Loading)
                emit(Result.StationList(radioRepository.getStations()))
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.Error(cause)); true }
    }
}