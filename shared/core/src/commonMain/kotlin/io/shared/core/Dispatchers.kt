package io.shared.core

import kotlinx.coroutines.CoroutineDispatcher

expect val MainDispatcher: CoroutineDispatcher
expect val IoDispatcher: CoroutineDispatcher
