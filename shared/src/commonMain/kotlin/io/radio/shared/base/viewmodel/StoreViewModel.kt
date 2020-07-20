package io.radio.shared.base.viewmodel

import io.radio.shared.base.Logger
import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Store
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class StoreViewModel<A, S : Persistable, E> : ViewModel() {

    abstract val store: Store<A, S, E>

    init {
        scope.launch(CoroutineExceptionHandler { _, throwable ->
            Logger.e("Store", "Unhandled exception", throwable)
        }) {
            store.wire()
        }
    }

}