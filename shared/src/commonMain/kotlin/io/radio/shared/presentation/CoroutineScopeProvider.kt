package io.shared.presentation

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {

    val scope: CoroutineScope

}