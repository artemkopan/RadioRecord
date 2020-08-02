package io.radio.shared.base.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Middleware<Action : Any, Result : Any, State : Any> {

    fun accept(actions: Flow<Action>, state: StateFlow<State>): Flow<Result>

}