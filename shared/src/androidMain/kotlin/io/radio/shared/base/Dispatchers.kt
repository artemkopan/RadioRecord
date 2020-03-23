package io.radio.shared.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val MainDispatcher: CoroutineDispatcher get() = Dispatchers.Main.immediate
actual val IoDispatcher: CoroutineDispatcher get() = Dispatchers.IO
