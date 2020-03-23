package io.radio.shared.base.viewmodel

import androidx.lifecycle.ViewModel
import io.radio.shared.base.State
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking

typealias AndroidViewModel = ViewModel

actual open class ViewModel actual constructor() : AndroidViewModel() {

    protected actual val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    actual override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    protected actual inline fun <T : Any> SendChannel<State<T>>.perform(crossinline onLoad: suspend () -> T) {
        sendBlocking(State.Loading)
        scope.launch(CoroutineExceptionHandler { _, throwable -> sendBlocking(State.Fail(throwable)) }) {
            sendBlocking(State.Success(onLoad()))
        }
    }

}
