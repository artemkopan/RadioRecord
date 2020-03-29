package io.radio.shared.base.recycler

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistry

class RecyclerViewStateController(
    viewLifecycleOwner: LifecycleOwner,
    private val savedStateRegistry: SavedStateRegistry,
    private val recyclerView: RecyclerView
) {

    init {
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                savedStateRegistry.registerSavedStateProvider(REGISTRY) {
                    Bundle(1).apply {
                        getLayoutManager().onSaveInstanceState()?.let {
                            putParcelable(KEY_STATE, it)
                        }
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                savedStateRegistry.unregisterSavedStateProvider(REGISTRY)
            }
        })
    }

    fun restoreState(): Boolean {
        savedStateRegistry.consumeRestoredStateForKey(REGISTRY)
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