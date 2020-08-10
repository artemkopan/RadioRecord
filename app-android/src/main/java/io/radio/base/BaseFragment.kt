@file:Suppress("NOTHING_TO_INLINE")

package io.radio.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import io.shared.core.CoroutineScopeProvider
import io.shared.core.MainDispatcher
import io.shared.core.extensions.lazyNonSafety
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

@ContainerOptions(cache = CacheImplementation.NO_CACHE)
open class BaseFragment : Fragment, CoroutineScopeProvider {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override val scope by lazyNonSafety {
        CoroutineScope(SupervisorJob() + MainDispatcher)
    }

    override fun onDestroyView() {
        scope.coroutineContext.cancelChildren()
        super.onDestroyView()
    }

}

inline fun BaseFragment.popBack() = requireActivity().onBackPressedDispatcher.onBackPressed()

inline infix fun BaseFragment.showToast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
