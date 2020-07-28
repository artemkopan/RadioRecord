@file:Suppress("RemoveExplicitTypeArguments")

package io.radio.shared.base.viewmodel.koin

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.base.viewmodel.ViewModel
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Koin.viewModel(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazyNonSafety {
        getViewModel<T>(
            owner,
            qualifier,
            parameters
        )
    }
}

inline fun <reified T : ViewModel> Koin.getViewModel(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(owner, T::class, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(
    owner: ViewModelStoreOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getViewModel(owner, clazz, qualifier, parameters)
}

fun <T : ViewModel> Koin.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    return _scopeRegistry.rootScope.getViewModel(viewModelParameters)
}


inline fun <reified T : ViewModel> Koin.stateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getStateViewModel<T>(owner, qualifier, bundle, parameters)
    }
}

inline fun <reified T : ViewModel> Koin.getStateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getStateViewModel(owner, T::class, qualifier, bundle, parameters)
}

fun <T : ViewModel> Koin.getStateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): T {
    return _scopeRegistry.rootScope.getStateViewModel(owner, clazz, qualifier, bundle, parameters)
}