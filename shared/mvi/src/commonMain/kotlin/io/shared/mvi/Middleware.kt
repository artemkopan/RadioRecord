package io.shared.mvi

import kotlinx.coroutines.flow.Flow

interface Middleware<Action : Any, Result : Any, State : Any> {

    fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result>

}