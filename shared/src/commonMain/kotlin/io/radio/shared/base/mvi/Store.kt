package io.radio.shared.base.mvi

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Logger
import io.radio.shared.base.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


interface Store<Action : Any, Result : Any, State : Persistable> {

    val stateFlow: StateFlow<State>

    fun dispatchAction(action: Action)
}

open class StoreImpl<Action : Any, Result : Any, State : Persistable>(
    private val coroutineScope: CoroutineScope,
    private val middlewareList: List<Middleware<Action, Result, State>>,
    private val bootstrapperList: List<Bootstrapper<Action, Result, State>>,
    private val reducer: Reducer<Result, State>,
    initialState: State
) : Store<Action, Result, State> {

    private val stateMutableFlow = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<State>
        get() = stateMutableFlow

    private val actions = BroadcastChannel<Action>(1)
    private val results = BroadcastChannel<Result>(1)

    init {
        wire()
    }

    override fun dispatchAction(action: Action) {
        coroutineScope.launch {
            actions.send(action).also { Logger.d(TAG, "Dispatch action: $action") }
        }
    }

    private fun wire() {
        coroutineScope.launch(IoDispatcher) {
            val actionsFlow = actions.asFlow()
            val resultsFlow = results.asFlow()

            bootstrapperList.map { it.accept(actionsFlow, resultsFlow, stateMutableFlow) }
                .merge()
                .onEach {
                    dispatchAction(it)
                }
                .launchIn(this)

            middlewareList.map { it.accept(actionsFlow, stateMutableFlow) }
                .merge()
                .onEach { result ->
                    launch { results.send(result) }
                    val state = stateMutableFlow.value
                    stateMutableFlow.value = reducer.reduce(result, state).also {
                        Logger.d(TAG, "Reduce result: $it, old state -> $state, new sate -> $it")
                    }
                }
                .launchIn(this)
        }
    }

    private companion object {
        const val TAG = "Store"
    }
}