package io.radio.shared.common.viewmodel

import io.radio.shared.common.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel

expect open class ViewModel() {

    protected val scope: CoroutineScope

    protected open fun onCleared()

    protected inline fun <T : Any> SendChannel<State<T>>.perform(crossinline onLoad: suspend () -> T)

}

