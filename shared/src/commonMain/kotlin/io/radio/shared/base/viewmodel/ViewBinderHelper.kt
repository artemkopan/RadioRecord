@file:Suppress("RedundantSuspendModifier")

package io.radio.shared.base.viewmodel

import io.radio.shared.base.Event
import io.radio.shared.base.Persistable
import io.radio.shared.base.asEvent
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

class ViewBinderHelper<Model : Any, Effect : Persistable>(private val stateStorage: StateStorage) {

    private val modelMutableStateFlow = MutableStateFlow<Model?>(null)
    val modelFlow: Flow<Model> = modelMutableStateFlow.filterNotNull()

    private val effectChannel = ConflatedBroadcastChannel<Event<Effect>>()
    val effectFlow: Flow<Effect> = effectChannel.asFlow().mapNotNull { it.getContentIfNotHandled() }

    suspend fun dispatchModel(model: Model) {
        modelMutableStateFlow.value = model
    }

    suspend fun dispatchEffect(effect: Effect) {
        effectChannel.send(effect.asEvent())
    }

}