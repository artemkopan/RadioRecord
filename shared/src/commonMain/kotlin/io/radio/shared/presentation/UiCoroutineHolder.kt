package io.radio.shared.presentation

import kotlinx.coroutines.CoroutineScope

interface UiCoroutineHolder {

    val viewScope: CoroutineScope

}