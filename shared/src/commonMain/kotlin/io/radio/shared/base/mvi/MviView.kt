package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView<A, S : Persistable, E : Persistable> {

    val actions: Flow<A>

    fun render(state: S)

    fun sideEffect(sideEffect: E) {
        //no-op
    }

}