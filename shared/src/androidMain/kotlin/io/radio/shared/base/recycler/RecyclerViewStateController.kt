package io.radio.shared.base.recycler

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistryOwner
import io.radio.shared.base.Logger

class RecyclerViewStateController(
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val layoutManagerCall: () -> RecyclerView.LayoutManager?
) {

    private val savedStateRegistry get() = savedStateRegistryOwner.savedStateRegistry

    //Use it when savedState is not called and a list is restored in async way
    private var temporaryState: Parcelable? = null

    init {
        savedStateRegistryOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                savedStateRegistry.registerSavedStateProvider(REGISTRY) {
                    Bundle(1).apply {
                        getLayoutManager()?.onSaveInstanceState()?.let {
                            putParcelable(KEY_STATE, it)
                        }
                    }
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                temporaryState = getLayoutManager()?.onSaveInstanceState()
            }

        })
    }

    fun restoreState(): Boolean {
        (restoreParcelable() ?: temporaryState)?.let {
            getLayoutManager()?.onRestoreInstanceState(it) ?: return false
            return true
        }
        return false
    }

    private fun restoreParcelable() = savedStateRegistry.consumeRestoredStateForKey(REGISTRY)
        ?.getParcelable<Parcelable>(KEY_STATE)

    private fun getLayoutManager(): RecyclerView.LayoutManager? {
        return layoutManagerCall().also {
            if (it == null) {
                Logger.e("Set up a layout manager first")
            }
        }
    }

    companion object {
        private const val REGISTRY = "RecyclerViewStateController"
        private const val KEY_STATE = "key-state"
    }
}