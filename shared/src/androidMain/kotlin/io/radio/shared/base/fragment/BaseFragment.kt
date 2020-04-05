@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.base.fragment

import androidx.fragment.app.Fragment
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.presentation.UiCoroutineHolder
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ContainerOptions(cache = CacheImplementation.NO_CACHE)
open class BaseFragment : Fragment, UiCoroutineHolder {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override val scope by lazyNonSafety {
        CoroutineScope(SupervisorJob() + MainDispatcher)
    }

    override fun onDestroyView() {
        scope.coroutineContext.cancelChildren()
        super.onDestroyView()
    }

    fun <T> Flow<T>.subscribe(action: suspend (T) -> Unit) =
        conflate().onEach(action).launchIn(scope)
}

inline fun BaseFragment.popBack() = requireActivity().onBackPressedDispatcher.onBackPressed()

