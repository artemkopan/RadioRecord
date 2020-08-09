package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.CoroutineScope


interface StoreFactory<Action : Any, Result : Any, State : Persistable> {

    fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): Store<Action, Result, State>

}

