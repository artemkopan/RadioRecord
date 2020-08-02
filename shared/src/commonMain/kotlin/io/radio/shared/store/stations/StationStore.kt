package io.radio.shared.store.stations

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Store
import io.radio.shared.model.Station
import io.radio.shared.store.stations.StationStore.*

interface StationStore : Store<Action, Result, State> {

    sealed class Action {

        object LoadStations : Action()

        data class PlayStation(val station: Station): Action()

    }

    sealed class Result {

        object Loading : Result()
        data class Success(val data: List<Station>) : Result()
        data class Error(val throwable: Throwable) : Result()

    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val data: List<Station> = emptyList()
    ) : Persistable

}


