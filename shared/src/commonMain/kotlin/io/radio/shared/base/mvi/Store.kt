package io.radio.shared.base.mvi

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext


interface Store<A, S : Persistable, E : Persistable> {
    suspend fun bind(mviView: MviView<A, S, E>)
}

class StoreImpl<A, S : Persistable, E : Persistable>(
    private val reducer: Reducer<S, A, E>,
    private val middlewareList: List<Middleware<A, S>>,
    initialState: S,
    coroutineScope: CoroutineScope
) : Store<A, S, E> {

    private val stateFlow = MutableStateFlow(initialState)
    private val sideEffectsFlow = MutableStateFlow<E?>(null)
    private val actions = BroadcastChannel<A>(1)

    init {
        coroutineScope.launch { wire() }
    }

    override suspend fun bind(mviView: MviView<A, S, E>) {
        withContext(MainDispatcher) {
            stateFlow.onEach { mviView.render(it) }.launchIn(this)
            sideEffectsFlow.onEach { it?.let { mviView.sideEffect(it) } }.launchIn(this)
            mviView.actions.onEach { actions.send(it) }.launchIn(this)
        }
    }

    private suspend fun wire() {
        withContext(IoDispatcher) {
            val actionsFlow = actions.asFlow()
            supervisorScope {
                actionsFlow
                    .onEach {
                        stateFlow.value = reducer.reduce(stateFlow.value, it, sideEffectsFlow)
                    }
                    .launchIn(this)
            }
            supervisorScope {
                merge(*middlewareList.map { it.dispatch(actionsFlow, stateFlow) }
                    .toTypedArray())
                    .onEach { actions.send(it) }
                    .launchIn(this)
            }
        }
    }
}