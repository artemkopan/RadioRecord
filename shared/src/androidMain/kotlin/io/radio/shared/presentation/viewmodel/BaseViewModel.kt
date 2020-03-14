package io.radio.shared.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.radio.shared.presentation.State
import kotlinx.coroutines.*

open class BaseViewModel : ViewModel() {

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }


    protected inline fun <T : Any> MutableLiveData<State<T>>.launch(crossinline onLoad: suspend () -> T) =
        scope.launch(CoroutineExceptionHandler { _, throwable -> value = State.Fail(throwable) }) {
            value = State.Loading
            value = State.Success(onLoad())
        }
}
