@file:Suppress("UNCHECKED_CAST")

package io.radio.di

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewAmbient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import io.shared.core.Logger
import io.shared.mvi.StateStorage
import org.kodein.di.DIAware
import org.kodein.di.direct
import org.kodein.di.instance

typealias DefaultArguments = () -> Bundle?

inline fun <reified VM : ViewModel> ComponentActivity.viewBinder(noinline defaultArgs: DefaultArguments? = null): Lazy<VM> {
    return viewModels(factoryProducer = {
        val diAware = applicationContext as DIAware
        diAware.getFactoryInstance<VM>(this, defaultArgs?.invoke() ?: Bundle.EMPTY)
    })
}

inline fun <reified VM : ViewModel> Fragment.viewBinder(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline defaultArgs: DefaultArguments? = null
): Lazy<VM> {
    return viewModels(ownerProducer = ownerProducer, factoryProducer = {
        val diAware = requireContext().applicationContext as DIAware
        diAware.getFactoryInstance<VM>(this, defaultArgs?.invoke() ?: arguments ?: Bundle.EMPTY)
    })
}

@Composable
inline fun <reified VM : ViewModel> viewBinderCompose(
    noinline defaultArgs: DefaultArguments? = null
): VM {
    val diAware = androidx.compose.ui.platform.ContextAmbient.current.applicationContext as DIAware
    val stateRegistryOwner = ViewTreeSavedStateRegistryOwner.get(ViewAmbient.current)
    val factory = diAware.getFactoryInstance<VM>(
        stateRegistryOwner!!, defaultArgs?.invoke() ?: Bundle.EMPTY
    )
    return androidx.compose.ui.viewinterop.viewModel(VM::class.java, null, factory)
}

inline fun <reified VM : ViewModel> DIAware.getFactoryInstance(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            Logger.d(message = "Model class: $modelClass", throwable = null, tag = "Test")
            return di.direct.instance<StateStorage, VM>(arg = StateStorage(handle)) as T
        }
    }
}

