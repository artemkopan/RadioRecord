package io.radio.shared.base.viewmodel

import io.radio.shared.base.MainDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.native.concurrent.ThreadLocal
import kotlin.native.internal.GC

@ThreadLocal
private var isGCWorking = false

actual open class ViewBinder actual constructor() {

    protected actual val scope: CoroutineScope = CoroutineScope(MainDispatcher)

    protected actual open fun onDestroy() {
        scope.cancel()
        // run Kotlin/Native GC
        if (!isGCWorking) {
            isGCWorking = true
            GC.collect()
            isGCWorking = false
        }
    }

}