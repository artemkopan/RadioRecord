package io.radio.shared.base.mvi.middleware

import io.radio.shared.base.Logger
import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class LoggingMiddleware<A, S : Persistable> : Middleware<A, S> {

    override fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>
    ): Flow<A> {
        return merge(
            actions.transform {
                Logger.d(TAG, "Dispatch action: $actions, current state: ${states.value}")
                return@transform
            },
            states.transform {
                Logger.d(TAG, "Dispatch state: $it")
                return@transform
            }
        )
    }

    companion object {
        private const val TAG = "Mvi"
    }
}