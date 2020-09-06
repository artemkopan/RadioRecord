package io.shared.mvi

import io.shared.core.IoDispatcher
import io.shared.core.Logger
import io.shared.core.Persistable
import io.shared.core.getLogMessageOrDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


interface Store<Action : Any, Result : Any, State : Persistable> {

    val stateFlow: Flow<State>

    fun dispatchAction(action: Action)
}

open class StoreImpl<Action : Any, Result : Any, State : Persistable>(
    private val tag: String,
    private val coroutineScope: CoroutineScope,
    private val middlewareList: List<Middleware<Action, Result, State>>,
    private val bootstrapperList: List<Bootstrapper<Action, Result, State>>,
    private val reducer: Reducer<Result, State>,
    initialState: State
) : Store<Action, Result, State> {

    private val stateChannel = ConflatedBroadcastChannel(initialState)
    override val stateFlow: Flow<State>
        get() = stateChannel.asFlow()

    private val actions = BroadcastChannel<Action>(1)

    init {
        wire()
    }

    override fun dispatchAction(action: Action) {
        coroutineScope.launch {
            actions.send(action).also { Logger.d("Dispatch action: $action", tag = tag) }
        }
    }

    private fun wire() {
        coroutineScope.launch(IoDispatcher) {
            val actionsFlow = actions.asFlow()

            bootstrapperList.map { it.accept(actionsFlow) { stateChannel.value } }
                .merge()
                .onEach {
                    dispatchAction(it)
                }
                .launchIn(this)

            middlewareList.map { it.accept(actionsFlow) { stateChannel.value } }
                .merge()
                .onEach { result ->

                    val state = stateChannel.value
                    stateChannel.send(reducer.reduce(result, state)
                        .also {
                            Logger.d(
                                "Reduce result: ${result.getLogMessageOrDefault()}, old state -> ${state.getLogMessageOrDefault()}, new sate -> ${it.getLogMessageOrDefault()}",
                                tag = tag
                            )
                        })
                }
                .launchIn(this)
        }
    }

}