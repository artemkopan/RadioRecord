package io.radio.shared.base.mvi

import kotlinx.coroutines.flow.Flow

interface MviView<A, S, E> {

    val actions: Flow<A>

    fun render(state: S)

    fun event(event: E) {
        //no-op
    }

}