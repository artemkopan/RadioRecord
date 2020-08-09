package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView<Intent : Any, Model : Persistable, Event : Persistable> :
    ViewRenderer<Model>,
    ViewEffects<Event> {

    val intents: Flow<Intent>

}