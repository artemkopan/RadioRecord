package io.radio.shared.base.mvi

import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import kotlinx.coroutines.flow.Flow

interface Middleware2<R : Result> {

    fun dispatch(actionFlow: Flow<Action>): Flow<R>

}

