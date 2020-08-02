package io.radio.shared.base.mvi

interface Reducer<Result, State> {

    fun reduce(result: Result, state: State): State

}