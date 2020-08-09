package io.shared.core

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {

    val scope: CoroutineScope

}