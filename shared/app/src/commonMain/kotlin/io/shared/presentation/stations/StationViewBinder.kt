package io.shared.presentation.stations

import io.shared.core.Logger
import io.shared.formatters.ErrorFormatter
import io.shared.mvi.Binder
import io.shared.mvi.StateStorage
import io.shared.mvi.ViewBinder
import io.shared.mvi.ViewBinderHelper
import io.shared.presentation.stations.StationView.*
import io.shared.store.stations.StationStore
import io.shared.store.stations.StationStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class StationViewBinder(
    stateStorage: StateStorage,
    storeFactory: StationStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder(), Binder<Intent, Model, Effect> {

    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)
    private val store = storeFactory.create(scope, stateStorage)

    init {
        store.stateFlow
            .onEach {
                Logger.d("Receive state = $it", tag = "StationViewBinder")
                it.dispatchModel()
                it.dispatchEffect()
            }.launchIn(scope)
    }

    override fun bindIntents(
        scope: CoroutineScope,
        intentFlow: Flow<Intent>
    ) {
        intentFlow.mapToAction().bindTo(store, scope)
    }

    override val modelFlow: Flow<Model>
        get() = helper.modelFlow

    override val effectFlow: Flow<Effect>
        get() = helper.effectFlow

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