package io.radio.shared.base.mvi.v3

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext


class Store<Action, State : Persistable>(
    private val coroutineScope: CoroutineScope,
    private val middlewareList: List<Middleware<Action, State>>,
    private val reducer: Reducer<Action, State>,
    initialState: State
) {

    private val stateMutableFlow = MutableStateFlow(initialState)
    val stateFlow: StateFlow<State> get() = stateMutableFlow

    private val actions = BroadcastChannel<Action>(1)

    init {
        coroutineScope.launch { wire() }
    }

    fun dispatchAction(action: Action) {
        coroutineScope.launch { actions.send(action) }
    }

    private suspend fun wire() {
        withContext(IoDispatcher) {
            val actionsFlow = actions.asFlow()
            supervisorScope {
                actionsFlow
                    .onEach {
                        stateMutableFlow.value = reducer.invoke(stateMutableFlow.value, it)
                    }
                    .launchIn(this)
            }
            supervisorScope {
                middlewareList.map { it.invoke(actionsFlow, stateMutableFlow) }
                    .merge()
                    .onEach { actions.send(it) }
                    .launchIn(this)
            }
        }
    }
}