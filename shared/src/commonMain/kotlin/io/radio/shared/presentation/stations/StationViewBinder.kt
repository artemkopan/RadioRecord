package io.radio.shared.presentation.stations

import io.radio.shared.base.extensions.formatTag
import io.radio.shared.base.mvi.bind
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewBinder
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.store.stations.StationStore
import io.radio.shared.store.stations.StationStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class StationViewBinder(
    stateStorage: StateStorage,
    storeFactory: StationStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder() {

    private val store = storeFactory.create(scope, stateStorage)

    suspend fun attachView(view: StationView) {
        bind {
            store.stateFlow.mapToModel() bindTo view
            store.stateFlow.mapStationStateToEvent() bindTo view

            view.intents.mapToAction() bindTo store
            view.intents.mapStationIntentToEvent() bindTo view
        }
    }

    private fun Flow<StationStore.State>.mapToModel() = map {
        StationView.Model(
            it.isLoading,
            it.data
        )
    }

    private fun Flow<StationStore.State>.mapStationStateToEvent() = mapNotNull {
        it.error?.let { throwable ->
            StationView.Event.Error(
                errorFormatter.format(throwable),
                throwable formatTag "load_stations"
            )
        }
    }

    private fun Flow<StationView.Intent>.mapToAction() = mapNotNull {
        if (it is StationView.Intent.SelectStation) {
            StationStore.Action.PlayStation(it.station)
        } else {
            null
        }
    }

    private fun Flow<StationView.Intent>.mapStationIntentToEvent() = mapNotNull {
        if (it is StationView.Intent.SelectStation) {
            StationView.Event.NavigateToPlayer
        } else {
            null
        }
    }

}