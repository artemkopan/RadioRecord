package io.radio.shared.presentation.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import io.radio.shared.common.viewmodel.ViewModel
import org.koin.core.error.DefinitionParameterException
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope


internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return get(viewModelParameters, viewModelParameters.qualifier, javaClass)
}

internal fun <T : ViewModel> ViewModelProvider.get(
    viewModelParameters: ViewModelParameter<T>,
    qualifier: Qualifier?,
    javaClass: Class<T>
): T {
    return if (viewModelParameters.qualifier != null) {
        get(qualifier.toString(), javaClass)
    } else {
        get(javaClass)
    }
}

internal fun <T : ViewModel> Scope.createViewModelProvider(
    viewModelParameters: ViewModelParameter<T>
): ViewModelProvider {
    return ViewModelProvider(
        viewModelParameters.viewModelStore,
        if (viewModelParameters.bundle != null) {
            stateViewModelFactory(viewModelParameters)
        } else {
            defaultViewModelFactory(viewModelParameters)
        }
    )
}


/**
 * Create Bundle/State ViewModel Factory
 */
fun <T : ViewModel> Scope.stateViewModelFactory(
    vmParams: ViewModelParameter<T>
): AbstractSavedStateViewModelFactory {
    val registryOwner = (vmParams.stateRegistryOwner
        ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"))
    return object : AbstractSavedStateViewModelFactory(registryOwner, vmParams.bundle) {
        override fun <T : androidx.lifecycle.ViewModel?> create(
            key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            return get(
                vmParams.clazz,
                vmParams.qualifier
            ) { parametersOf(*insertStateParameter(handle)) }
        }

        private fun insertStateParameter(handle: SavedStateHandle): Array<out Any?> {
            val parameters: DefinitionParameters = vmParams.parameters?.invoke() ?: emptyParametersHolder()
            val values = parameters.values.toMutableList()
            if (values.size > 4) {
                throw DefinitionParameterException("Can't add SavedStateHandle to your definition function parameters, as you already have ${values.size} elements: $values")
            }

            values.add(0, handle)
            return values.toTypedArray()
        }

    }
}

/**
 * Create Default ViewModel Factory
 */
fun <T : ViewModel> Scope.defaultViewModelFactory(parameters: ViewModelParameter<T>): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
            return get(parameters.clazz, parameters.qualifier, parameters.parameters)
        }
    }
}