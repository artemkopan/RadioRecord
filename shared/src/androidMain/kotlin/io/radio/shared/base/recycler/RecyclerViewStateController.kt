package io.radio.shared.base.recycler

import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistryOwner

class RecyclerViewStateController(
    private val stateRegistryOwner: SavedStateRegistryOwner,
    private val recyclerView: RecyclerView
) {

    init {
        stateRegistryOwner.savedStateRegistry.registerSavedStateProvider(REGISTRY) {
            Bundle(1).apply {
                getLayoutManager().onSaveInstanceState()?.let {
                    putParcelable(KEY_STATE, it)
                }
            }
        }
    }

    fun restoreState(): Boolean {
        stateRegistryOwner.savedStateRegistry.consumeRestoredStateForKey(REGISTRY)
            ?.getParcelable<Parcelable>(KEY_STATE)
            ?.let {
                getLayoutManager().onRestoreInstanceState(it)
                return true
            }
        return false
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return requireNotNull(recyclerView.layoutManager) {
            "Set up a layout manager first"
        }
    }

    companion object {
        private const val REGISTRY = "RecyclerViewStateController"
        private const val KEY_STATE = "key-state"
    }
}