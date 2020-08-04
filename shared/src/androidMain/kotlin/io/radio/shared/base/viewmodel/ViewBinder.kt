package io.radio.shared.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

typealias AndroidViewModel = ViewModel

actual open class ViewBinder actual constructor() : AndroidViewModel() {

    protected actual val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected actual open fun onDestroy() {
        scope.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }

}
