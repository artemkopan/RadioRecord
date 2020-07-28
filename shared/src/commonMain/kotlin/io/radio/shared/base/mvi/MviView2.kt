package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView2<Intent, State : Persistable, Signal : Persistable> {

    val intents: Flow<Intent>

    fun render(state: State)

    fun applySignal(signal: Signal) {
        //no-op
    }

}