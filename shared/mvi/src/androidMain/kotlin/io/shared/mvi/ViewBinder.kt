package io.shared.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

typealias AndroidViewModel = ViewModel

actual open class ViewBinder : AndroidViewModel() {

    protected actual val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @CallSuper
    protected actual open fun onDestroy() {
        scope.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }

}
