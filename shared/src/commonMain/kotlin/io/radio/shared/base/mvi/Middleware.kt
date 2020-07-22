package io.radio.shared.base.mvi

import io.radio.shared.base.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Middleware<A, S, E> {

    fun dispatch(actions: Flow<A>, states: StateFlow<S>, events: MutableStateFlow<Event<E>?>): Flow<A>

}

