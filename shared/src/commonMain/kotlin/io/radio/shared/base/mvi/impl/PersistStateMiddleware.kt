package io.radio.shared.base.mvi.impl

import io.radio.shared.base.Event
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class PersistStateMiddleware<A, S, E>(
    private val key: String,
    private val stateStorage: StateStorage
) : Middleware<A, S, E> {

    override fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>,
        events: StateFlow<Event<E>?>
    ): Flow<A> {
        return states.transform {
            stateStorage[key] = it
            return@transform
        }
    }

}