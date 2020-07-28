package io.radio.shared.base.mvi.v4

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.v3.Middleware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext


interface Store<Intent, State : Persistable, Signal : Persistable> {
    suspend fun bind(mviView: MviView<Intent, State, Signal>)
}

class StoreImpl<Intent, State : Persistable, Signal : Persistable>(
    private val middlewareList: List<Middleware<Intent, State>>,
    initialState: State,
    coroutineScope: CoroutineScope
) : Store<Intent, State, Signal> {

    private val stateFlow = MutableStateFlow(initialState)
    private val signalFlow = MutableStateFlow<Signal?>(null)
    private val intents = BroadcastChannel<Intent>(1)

    init {
        coroutineScope.launch { wire() }
    }

    override suspend fun bind(mviView: MviView<Intent, State, Signal>) {
        withContext(MainDispatcher) {
            stateFlow.onEach { mviView.render(it) }.launchIn(this)
            signalFlow.onEach { it?.let { mviView.applySignal(it) } }.launchIn(this)
            mviView.intents.onEach { intents.send(it) }.launchIn(this)
        }
    }

    private suspend fun wire() {
        withContext(IoDispatcher) {
            val actionsFlow = intents.asFlow()
            supervisorScope {
                actionsFlow
                    .onEach {
                        stateFlow.value = reducer.reduce(stateFlow.value, it, signalFlow)
                    }
                    .launchIn(this)
            }
            supervisorScope {
                middlewareList.map { it.invoke(actionsFlow, stateFlow) }
                    .onEach { intents.send(it) }
                    .launchIn(this)
            }
        }
    }
}