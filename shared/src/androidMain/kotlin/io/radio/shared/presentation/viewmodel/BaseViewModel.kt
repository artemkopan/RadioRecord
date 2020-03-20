package io.radio.shared.presentation.viewmodel

import androidx.lifecycle.ViewModel
import io.radio.shared.presentation.State
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking

open class BaseViewModel : ViewModel() {

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }


    protected inline fun <T : Any> SendChannel<State<T>>.perform(crossinline onLoad: suspend () -> T) {
        sendBlocking(State.Loading)
        scope.launch(CoroutineExceptionHandler { _, throwable -> sendBlocking(State.Fail(throwable)) }) {
            sendBlocking(State.Success(onLoad()))
        }
    }
}
