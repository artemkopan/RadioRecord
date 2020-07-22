package io.radio.shared.base.mvi.impl

import io.radio.shared.base.Event
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.feature.player.PlayerAction
import kotlinx.coroutines.flow.*

class PersistStateMiddleware<A, S, E>(
    private val key: String,
    private val stateStorage: StateStorage
) : Middleware<A, S, E> {

    override fun dispatch(
        actions: Flow<A>,
        states: StateFlow<S>,
        events: MutableStateFlow<Event<E>?>
    ): Flow<A> {
        actions.filter { it is PlayerAction.ForwardClicked }

        return states.transform {
            stateStorage[key] = it
            return@transform
        }
    }

}