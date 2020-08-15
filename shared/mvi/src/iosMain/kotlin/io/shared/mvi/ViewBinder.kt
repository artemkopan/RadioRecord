package io.shared.mvi

import io.shared.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.native.concurrent.ThreadLocal
import kotlin.native.internal.GC


@ThreadLocal
private var isGCWorking = false

actual open class ViewBinder actual constructor() {

    protected actual val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    protected actual open fun onDestroy() {
        Logger.d("destroy invoked", tag = "ViewBinder")
        scope.cancel()
        // run Kotlin/Native GC
        if (!isGCWorking) {
            isGCWorking = true
            GC.collect()
            isGCWorking = false
        }
    }

}