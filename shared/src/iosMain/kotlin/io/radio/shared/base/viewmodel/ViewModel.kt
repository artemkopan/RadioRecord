package io.radio.shared.base.viewmodel

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
private var isGCWorking = false

//actual open class ViewModel actual constructor() {
//    // for now dispatcher fixed on Main. after implementing multithread coroutines on native - we can change it
//    protected actual val viewModelScope: CoroutineScope = CoroutineScope(MainDispatcher)
//
//    actual open fun onCleared() {
//        viewModelScope.cancel()
//        // run Kotlin/Native GC
//        if (!isGCWorking) {
//            isGCWorking = true
//            GC.collect()
//            isGCWorking = false
//        }
//    }
//}