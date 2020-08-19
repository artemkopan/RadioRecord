@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "UNCHECKED_CAST")

package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


interface Binder<Intent : Any, Model : Persistable, Effect : Persistable> {

    fun bindIntents(
        scope: CoroutineScope,
        intentFlow: Flow<Intent>
    )

    val modelFlow: Flow<Model>
    val effectFlow: Flow<Effect>

    fun <Action : Any> Flow<Action>.bindTo(store: Store<Action, *, *>, scope: CoroutineScope) {
        onEach { store.dispatchAction(it) }.launchIn(scope)
    }
}