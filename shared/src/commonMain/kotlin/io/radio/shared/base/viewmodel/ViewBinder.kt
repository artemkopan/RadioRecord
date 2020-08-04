package io.radio.shared.base.viewmodel

import kotlinx.coroutines.CoroutineScope

expect open class ViewBinder() {

    protected val scope: CoroutineScope

    protected open fun onDestroy()

}

