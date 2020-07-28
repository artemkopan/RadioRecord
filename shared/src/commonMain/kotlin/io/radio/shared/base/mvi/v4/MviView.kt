package io.radio.shared.base.mvi.v4

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView<Intent, State : Persistable, Signal : Persistable> {

    val intents: Flow<Intent>

    fun render(state: State)

    fun applySignal(signal: Signal) {
        //no-op
    }

}