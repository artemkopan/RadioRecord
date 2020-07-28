package io.radio.shared.base.mvi

import kotlinx.coroutines.flow.MutableStateFlow


interface Reducer<S, A, E> {

    fun reduce(state: S, action: A, sideEffects: MutableStateFlow<E?>): S

}

