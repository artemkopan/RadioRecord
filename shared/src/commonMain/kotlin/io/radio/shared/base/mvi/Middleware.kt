package io.radio.shared.base.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Middleware<A, S, E> {

    fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>
    ): Flow<A>

}

