package io.shared.mvi

import io.shared.core.MainDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual open class ViewBinder actual constructor() {

    protected actual val scope: CoroutineScope = CoroutineScope(SupervisorJob() + MainDispatcher)

    protected actual open fun onDestroy() {
        scope.cancel()
    }

}