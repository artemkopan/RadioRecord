package io.radio.shared.base.mvi


interface Reducer<S, A> {

    fun reduce(state: S, action: A): S

}

