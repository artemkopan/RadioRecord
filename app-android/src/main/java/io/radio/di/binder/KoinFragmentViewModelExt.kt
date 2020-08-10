package io.radio.di.binder

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistryOwner
import io.shared.core.extensions.lazyNonSafety
import io.shared.mvi.ViewBinder
import org.koin.android.ext.android.getKoin


@MainThread
inline fun <reified VM : ViewBinder> Fragment.viewBinder(
    crossinline ownerProducer: () -> SavedStateRegistryOwner = { this },
    crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
) = lazyNonSafety {
    getKoin().getStateViewModel<VM>(ownerProducer(), null, argumentsProducer(), null)
}

@MainThread
inline fun <reified VM : ViewBinder> Fragment.parentFragmentViewModel(
    crossinline argumentsProducer: () -> Bundle = { arguments ?: android.os.Bundle.EMPTY }
) = viewBinder<VM>(ownerProducer = { parentFragment as SavedStateRegistryOwner })

@MainThread
inline fun <reified VM : ViewBinder> Fragment.activityViewModel(
    crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
) = viewBinder<VM>(ownerProducer = { requireActivity() })

