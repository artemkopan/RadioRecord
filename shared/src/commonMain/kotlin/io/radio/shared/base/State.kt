package io.radio.shared.base

@Deprecated("use mvi approach")
sealed class State<out T: Any> {

    data class Success<T: Any>(val result: T) : State<T>()
    data class Fail(val throwable: Throwable) : State<Nothing>()
    object Loading : State<Nothing>()

}

