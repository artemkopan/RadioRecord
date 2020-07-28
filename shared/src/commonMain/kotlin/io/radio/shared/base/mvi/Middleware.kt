package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Middleware<A, S : Persistable> {

    fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>
    ): Flow<A>

}

