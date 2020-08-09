package io.shared.mvi

import io.shared.core.MainDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
private var isGCWorking = false

actual open class ViewBinder actual constructor() {

    protected actual val scope: CoroutineScope = CoroutineScope(MainDispatcher)

    protected actual open fun onDestroy() {
        scope.cancel()
        // run Kotlin/Native GC
        if (!isGCWorking) {
            isGCWorking = true
//todo           GC.collect()
            isGCWorking = false
        }
    }

}