package io.shared.mvi

interface Reducer<Result, State> {

    fun reduce(result: Result, state: State): State

}