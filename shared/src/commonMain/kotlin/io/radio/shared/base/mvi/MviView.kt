package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView<Intent : Any, Model : Persistable, Event : Persistable> :
    ViewRenderer<Model>,
    ViewEffects<Event> {

    val intents: Flow<Intent>

}