@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.base.fragment

import android.widget.Toast
import androidx.fragment.app.Fragment
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.presentation.UiCoroutineHolder
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

@ContainerOptions(cache = CacheImplementation.NO_CACHE)
open class BaseFragment : Fragment, UiCoroutineHolder {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override val viewScope by lazyNonSafety {
        CoroutineScope(SupervisorJob() + MainDispatcher)
    }

    override fun onDestroyView() {
        viewScope.coroutineContext.cancelChildren()
        super.onDestroyView()
    }

}

inline fun BaseFragment.popBack() = requireActivity().onBackPressedDispatcher.onBackPressed()

inline infix fun BaseFragment.showToast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
