package io.shared.mvi

import io.shared.core.Logger
import io.shared.core.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface MviView<Intent : Any, Model : Persistable, Effect : Persistable> :
    ViewRenderer<Model>,
    ViewEffects<Effect> {

    val intents: Flow<Intent>

    fun CoroutineScope.attachBinder(viewBinder: Binder<Intent, Model, Effect>) {
        Logger.d("bind", tag = "TEST")
        launch { viewBinder.bindIntents(this, intents) }
        Logger.d("model", tag = "TEST")
        viewBinder.modelFlow.onEach { render(it) }.launchIn(this)
        Logger.d("effect", tag = "TEST")
        viewBinder.effectFlow.onEach { acceptEffect(it) }.launchIn(this)
    }

}
