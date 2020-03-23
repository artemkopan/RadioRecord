package io.radio.shared.base

import kotlinx.coroutines.CoroutineDispatcher

expect val MainDispatcher: CoroutineDispatcher
expect val IoDispatcher: CoroutineDispatcher
