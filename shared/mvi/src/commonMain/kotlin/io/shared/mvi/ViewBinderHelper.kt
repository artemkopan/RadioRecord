@file:Suppress("RedundantSuspendModifier")

package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlin.jvm.Volatile

class ViewBinderHelper<Model : Any, Effect : Persistable>(
    private val stateStorage: StateStorage //FIXME validate if effect was shown
) {

    private val modelMutableStateFlow = MutableStateFlow<Model?>(null)
    val modelFlow: Flow<Model> = modelMutableStateFlow.filterNotNull()

    private val effectChannel = ConflatedBroadcastChannel<SingleEvent<Effect>>()
    val effectFlow: Flow<Effect> = effectChannel.asFlow().mapNotNull { it.getContentIfNotHandled() }

    suspend fun dispatchModel(model: Model) {
        modelMutableStateFlow.value = model
    }

    suspend fun dispatchEffect(effect: Effect) {
        effectChannel.send(SingleEvent(effect))
    }

    private class SingleEvent<out T>(private val content: T) {

        @Volatile
        private var hasBeenHandled = false

        /**
         * Returns the content and prevents its use again.
         */
        fun getContentIfNotHandled(): T? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }
        }
    }

}