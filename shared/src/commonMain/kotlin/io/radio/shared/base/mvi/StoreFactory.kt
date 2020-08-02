package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.CoroutineScope


interface StoreFactory<Action : Any, Result : Any, State : Persistable> {

    fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): Store<Action, Result, State>

}

