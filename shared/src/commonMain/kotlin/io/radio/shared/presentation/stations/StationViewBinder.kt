package io.shared.presentation.stations

import io.shared.mvi.Binder
import io.shared.mvi.bind
import io.shared.mvi.StateStorage
import io.shared.mvi.ViewBinder
import io.shared.mvi.ViewBinderHelper
import io.radio.shared.formatters.ErrorFormatter
import io.shared.presentation.stations.StationView.*
import io.shared.store.stations.StationStore
import io.shared.store.stations.StationStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class StationViewBinder(
    stateStorage: StateStorage,
    storeFactory: StationStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder(), Binder<StationView> {

    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)
    private val store = storeFactory.create(scope, stateStorage)

    init {
        store.stateFlow.onEach {
            it.dispatchModel()
            it.dispatchEffect()
        }.launchIn(scope)
    }

    override suspend fun bind(view: StationView) {
        bind {
            helper bindTo view
            view.intents.mapToAction() bindTo store
        }
    }

    private suspend fun StationStore.State.dispatchModel() {
        helper.dispatchModel(Model(isLoading, data))
    }

    private suspend fun StationStore.State.dispatchEffect() {
        helper.dispatchEffect(
            when {
                error != null -> {
                    Effect.Error(errorFormatter.format(error))
                }
                playingStation != null -> {
                    Effect.NavigateToPlayer
                }
                else -> return
            }
        )
    }

    private fun Flow<Intent>.mapToAction() = mapNotNull {
        if (it is Intent.SelectStation) {
            StationStore.Action.PlayStation(it.station)
        } else {
            null
        }
    }
}