package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow

interface MviView<Intent : Any, Model : Persistable, Event : ViewEvent> :
    ViewRenderer<Model>,
    ViewEvents<Event> {

    val intents: Flow<Intent>

}