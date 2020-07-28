package io.radio.shared.base.viewmodel.koin

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistryOwner
import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.base.viewmodel.ViewModel
import org.koin.android.ext.android.getKoin


@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModel(
    crossinline ownerProducer: () -> SavedStateRegistryOwner = { this },
    crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
) = lazyNonSafety {
    getKoin().getStateViewModel<VM>(ownerProducer(), null, argumentsProducer(), null)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.parentFragmentViewModel(
    crossinline argumentsProducer: () -> Bundle = { arguments ?: android.os.Bundle.EMPTY }
) = viewModel<VM>(ownerProducer = { parentFragment as SavedStateRegistryOwner })

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModel(
    crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
) = viewModel<VM>(ownerProducer = { requireActivity() })

