@file:Suppress("UNCHECKED_CAST")

package io.radio.shared.base.mvi

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.savedstate.SavedStateRegistry
import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

class MaiViewInterceptor<A, S : Persistable, E : Persistable>(
    savedStateRegistry: SavedStateRegistry,
    private val actionsFlow: Flow<A>,
    private val onRender: (S) -> Unit,
    private val onSideEffect: (E) -> Unit
) : MviView<A, S, E> {

    private var sideEffectState: E? = null

    init {
        savedStateRegistry.unregisterSavedStateProvider(KEY)
        savedStateRegistry.registerSavedStateProvider(KEY) {
            sideEffectState?.let {
                bundleOf(ARG_SIDE_EFFECT to it)
            } ?: Bundle.EMPTY
        }
        sideEffectState = savedStateRegistry.consumeRestoredStateForKey(ARG_SIDE_EFFECT)
            ?.getSerializable(ARG_SIDE_EFFECT) as E?
    }

    override val actions: Flow<A>
        get() = actionsFlow

    override fun render(state: S) {
        onRender(state)
    }

    override fun sideEffect(sideEffect: E) {
        if (sideEffectState != sideEffect) {
            onSideEffect(sideEffect)
        }
    }

    companion object {
        private const val KEY = "mvi-view-handler"
        private const val ARG_SIDE_EFFECT = "mvi-view-side-effect"
    }
}