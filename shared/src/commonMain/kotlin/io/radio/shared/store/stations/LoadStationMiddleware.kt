package io.radio.shared.store.stations

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.stations.StationStore.*
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