package io.radio.shared.base.mvi

import io.radio.shared.base.Event
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class Store<A, S : Persistable, E>(
    private val reducer: Reducer<S, A>,
    private val middlewareList: List<Middleware<A, S, E>>,
    initialState: S
) {

    private val stateFlow = MutableStateFlow(initialState)
    private val eventFlow = MutableStateFlow<Event<E>?>(null)
    private val actions = BroadcastChannel<A>(1)

    suspend fun wire() = withContext(IoDispatcher) {
        val actionsFlow = actions.asFlow()
        supervisorScope {
            actionsFlow
                .onEach {
                    stateFlow.value = reducer.reduce(stateFlow.value, it)
                }
                .launchIn(this)
        }
        supervisorScope {
            merge(*middlewareList.map { it.dispatch(actionsFlow, stateFlow, eventFlow) }
                .toTypedArray())
                .onEach { actions.send(it) }
                .launchIn(this)
        }
    }

    suspend fun bind(mviView: MviView<A, S, E>) = withContext(MainDispatcher) {
        stateFlow.onEach { mviView.render(it) }.launchIn(this)
        eventFlow.onEach { it?.onData { event -> mviView.event(event) } }.launchIn(this)
        mviView.actions.onEach { actions.send(it) }.launchIn(this)
    }

}