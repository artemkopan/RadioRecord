package io.shared.store.stations

import io.shared.core.Persistable
import io.shared.mvi.Store
import io.radio.shared.model.Station
import io.shared.store.stations.StationStore.*

interface StationStore : Store<Action, Result, State> {

    sealed class Action {

        object LoadStations : Action()

        data class PlayStation(val station: Station) : Action()

    }

    sealed class Result {

        object Loading : Result()

        data class StationList(val data: List<Station>) : Result()

        data class PlayingStation(val station: Station?) : Result()

        data class Error(val throwable: Throwable) : Result()

    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val data: List<Station> = emptyList(),
        val playingStation: Station? = null
    ) : Persistable

}


