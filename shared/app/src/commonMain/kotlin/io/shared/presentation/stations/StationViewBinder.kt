package io.shared.presentation.stations

import io.shared.core.Logger
import io.shared.core.extensions.CoroutineExceptionHandler
import io.shared.formatters.ErrorFormatter
import io.shared.mvi.*
import io.shared.presentation.stations.StationView.*
import io.shared.store.stations.StationStore
import io.shared.store.stations.StationStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StationViewBinder(
    stateStorage: StateStorage,
    storeFactory: StationStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder(), Binder<StationView> {

    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)
    private val store = storeFactory.create(scope, stateStorage)

    init {
        Logger.d(message = "Init station binder2")

        store.stateFlow
            .onStart { Logger.d("Flow started") }
            .onCompletion { Logger.d("Flow completed", throwable = it) }
            .onEmpty { Logger.d("Flow empty") }
            .onEach {
                Logger.d("Receive state = $it", tag = "StationViewBinder")
                it.dispatchModel()
                it.dispatchEffect()
            }.launchIn(CoroutineScope(CoroutineExceptionHandler {
                Logger.e("Error in scope", throwable = it)
            }))
            .also {
                Logger.d("Launched in job: $it")
            }
    }

    override suspend fun bind(view: StationView) {
        Logger.d("bind view: $view")
        GlobalScope.launch {
            flow<String> {
                for (i in 0..10) emit(i.toString())
            }
                .onEach {
                    Logger.d("test flow: $it")
                }
                .collect()
        }

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