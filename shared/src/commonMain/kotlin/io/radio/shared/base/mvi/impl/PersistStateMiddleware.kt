package io.radio.shared.base.mvi.impl

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class PersistStateMiddleware<A, S : Persistable, E>(
    private val key: String,
    private val stateStorage: StateStorage
) : Middleware<A, S, E> {

    override fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>
    ): Flow<A> {
        return states.transform {
            stateStorage[key] = it
            return@transform
        }
    }

}