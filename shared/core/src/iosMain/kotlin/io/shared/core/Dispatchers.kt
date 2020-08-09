package io.shared.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val MainDispatcher: CoroutineDispatcher get() = Dispatchers.Main.immediate

//todo waiting on coroutines 1.4
actual val IoDispatcher: CoroutineDispatcher get() = Dispatchers.Unconfined
