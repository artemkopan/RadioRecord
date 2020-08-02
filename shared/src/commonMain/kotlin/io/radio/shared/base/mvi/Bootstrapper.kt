package io.radio.shared.base.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Bootstrapper<Action : Any, Result : Any, State : Any> {

    fun accept(actions: Flow<Action>, results: Flow<Result>, stateFlow: StateFlow<State>) : Flow<Action>

}