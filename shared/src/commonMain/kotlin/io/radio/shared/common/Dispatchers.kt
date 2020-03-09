package io.radio.shared.common

import kotlinx.coroutines.CoroutineDispatcher

expect val MainDispatcher: CoroutineDispatcher
expect val IoDispatcher: CoroutineDispatcher
