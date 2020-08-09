package io.shared.mvi

import kotlinx.coroutines.CoroutineScope

expect open class ViewBinder() {

    protected val scope: CoroutineScope

    protected open fun onDestroy()

}

