@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.presentation.fragment

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Lazy
import io.radio.shared.common.MainDispatcher
import io.radio.shared.common.lazyNonSafety
import io.radio.shared.common.viewmodel.factory.InjectingSavedStateViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject


open class BaseFragment : Fragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    @Inject
    lateinit var factory: Lazy<InjectingSavedStateViewModelFactory>

    protected val viewScope by lazyNonSafety { CoroutineScope(SupervisorJob() + MainDispatcher) }

    override fun onDestroyView() {
        super.onDestroyView()
        viewScope.coroutineContext.cancelChildren()
    }

    @MainThread
    protected inline fun <reified VM : ViewModel> Fragment.viewModels(
        crossinline ownerProducer: () -> ViewModelStoreOwner = { this },
        crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
    ) = createViewModelLazy(
        VM::class,
        { ownerProducer().viewModelStore },
        {
            verifyVMFactory();
            factory.get().create(this, argumentsProducer())
        }
    )

    @MainThread
    protected inline fun <reified VM : ViewModel> Fragment.parentFragmentViewModels(
        crossinline argumentsProducer: () -> Bundle = { arguments ?: android.os.Bundle.EMPTY }
    ) = createViewModelLazy(
        VM::class,
        { requireParentFragment().viewModelStore },
        { verifyVMFactory(); factory.get().create(this, argumentsProducer()) }
    )

    @MainThread
    protected inline fun <reified VM : ViewModel> Fragment.activityViewModels(
        crossinline argumentsProducer: () -> Bundle = { arguments ?: Bundle.EMPTY }
    ) = createViewModelLazy(
        VM::class,
        { requireActivity().viewModelStore },
        { verifyVMFactory(); factory.get().create(this, argumentsProducer()) }
    )

    fun verifyVMFactory() {
        if (!::factory.isInitialized) {
            throw RuntimeException("VM's factory is not initialized. Probably fragment was not injected!")
        }
    }

}

inline fun BaseFragment.popBack() = requireActivity().onBackPressedDispatcher.onBackPressed()

