@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.base.fragment

import androidx.fragment.app.Fragment
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.extensions.lazyNonSafety
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren


open class BaseFragment : Fragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    protected val viewScope by lazyNonSafety {
        CoroutineScope(SupervisorJob() + MainDispatcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewScope.coroutineContext.cancelChildren()
    }

}

inline fun BaseFragment.popBack() = requireActivity().onBackPressedDispatcher.onBackPressed()

