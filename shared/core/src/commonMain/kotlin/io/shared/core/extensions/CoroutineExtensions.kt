package io.shared.core.extensions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

@Suppress("FunctionName")
inline fun CoroutineExceptionHandler(
    crossinline onError: (Throwable) -> Unit
): CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    onError(throwable)
}


class JobRunner {

    private var job: Job? = null

    fun cancel() {
        job?.cancel()
    }

    fun runAndCancelPrevious(runner: () -> Job) {
        job?.cancel()
        job = runner()
    }

}