package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface Bootstrapper<Action : Any, Result : Any, State : Any> {

    fun accept(
        actionFlow: Flow<Action>,
        resultFlow: Flow<Result>,
        state: (State) -> Unit
    ): Flow<Action>

}


@Suppress("FunctionName", "unused")
fun <Action : Any, Result : Any, State : Persistable> StoreFactory<Action, Result, State>.SimpleBootstrapper(
    action: Action
): Bootstrapper<Action, Result, State> {
    return object : Bootstrapper<Action, Result, State> {
        override fun accept(
            actionFlow: Flow<Action>,
            resultFlow: Flow<Result>,
            state: (State) -> Unit
        ): Flow<Action> {
            return flowOf(action)
        }
    }
}