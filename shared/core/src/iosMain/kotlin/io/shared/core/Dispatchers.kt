package io.shared.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val MainDispatcher: CoroutineDispatcher = Dispatchers.Main

//todo waiting for coroutines 1.4
actual val IoDispatcher: CoroutineDispatcher = Dispatchers.Main
