package io.shared.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val MainDispatcher: CoroutineDispatcher = Dispatchers.Main

actual val IoDispatcher: CoroutineDispatcher = Dispatchers.Default
