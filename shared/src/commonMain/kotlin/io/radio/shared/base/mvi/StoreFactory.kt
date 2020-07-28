package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.CoroutineScope

interface StoreFactory<Intent, SideEffect, State : Persistable, Signal : Persistable> {

    fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): Store2Impl<Intent, SideEffect, State, Signal>

    fun throwNotImplemented(result: Result): State {
        throw NotImplementedError("Not implemented result $result")
    }

}

