package io.radio.shared.base.mvi.v3

import kotlinx.coroutines.flow.Flow

typealias Middleware<Action, State> = (actionFlow: Flow<Action>, stateFlow: Flow<State>) -> Flow<Action>
