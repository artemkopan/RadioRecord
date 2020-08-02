package io.radio.shared.base.viewmodel

import io.radio.shared.base.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel

expect open class ViewBinder() {

    protected val scope: CoroutineScope

    protected open fun onDestroy()

    protected inline fun <T : Any> SendChannel<State<T>>.perform(crossinline onLoad: suspend () -> T)

}

