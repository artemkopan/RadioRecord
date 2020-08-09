package io.radio.di.binder

import android.os.Bundle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import io.shared.mvi.ViewBinder
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass


inline fun <reified T : ViewBinder> Scope.getStateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getStateViewModel(owner, T::class, qualifier, bundle, parameters)
}

fun <T : ViewBinder> Scope.getStateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): T {
    val bundleOrDefault: Bundle = bundle ?: Bundle.EMPTY
    return getViewModel(
        ViewModelParameter(
            clazz,
            qualifier,
            parameters,
            bundleOrDefault,
            owner.getViewModelStore(),
            owner
        )
    )
}

private fun SavedStateRegistryOwner.getViewModelStore(): ViewModelStore {
    return when (this) {
        is ViewModelStoreOwner -> this.viewModelStore
        else -> error("getStateViewModel error - Can't get ViewModelStore from $this")
    }
}

inline fun <reified T : ViewBinder> Scope.stateViewModel(
    owner: SavedStateRegistryOwner,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(owner, T::class, qualifier, bundle, parameters) }
}

fun <T : ViewBinder> Scope.stateViewModel(
    owner: SavedStateRegistryOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getStateViewModel(owner, clazz, qualifier, bundle, parameters) }
}



inline fun <reified T : ViewBinder> Scope.viewBinder(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { getViewModel(owner, T::class, qualifier, parameters) }
}

inline fun <reified T : ViewBinder> Scope.getViewModel(
    owner: ViewModelStoreOwner,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(owner, T::class, qualifier, parameters)
}

fun <T : ViewBinder> Scope.getViewModel(
    owner: ViewModelStoreOwner,
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return getViewModel(
        ViewModelParameter(
            clazz,
            qualifier,
            parameters,
            null,
            owner.viewModelStore,
            null
        )
    )
}

fun <T : ViewBinder> Scope.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    val viewModelProvider = createViewModelProvider(viewModelParameters)
    return viewModelProvider.resolveInstance(viewModelParameters)
}


class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    val parameters: ParametersDefinition? = null,
    val bundle: Bundle? = null,
    val viewModelStore: ViewModelStore,
    val stateRegistryOwner: SavedStateRegistryOwner? = null
)