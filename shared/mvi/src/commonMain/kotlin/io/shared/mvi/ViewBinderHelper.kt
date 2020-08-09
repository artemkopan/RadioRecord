@file:Suppress("RedundantSuspendModifier")

package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlin.jvm.Volatile

class ViewBinderHelper<Model : Any, Effect : Persistable>(private val stateStorage: StateStorage) {

    private val modelMutableStateFlow = MutableStateFlow<Model?>(null)
    val modelFlow: Flow<Model> = modelMutableStateFlow.filterNotNull()

    private val effectChannel = ConflatedBroadcastChannel<Event<Effect>>()
    val effectFlow: Flow<Effect> = effectChannel.asFlow().mapNotNull { it.getContentIfNotHandled() }

    suspend fun dispatchModel(model: Model) {
        modelMutableStateFlow.value = model
    }

    suspend fun dispatchEffect(effect: Effect) {
        effectChannel.send(Event(effect))
    }

    private class Event<out T>(private val content: T) {

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

        inline fun consume(crossinline block: (T) -> Unit) {
            getContentIfNotHandled()?.let(block)
        }

        /**
         * Returns the content, even if it's already been handled.
         */
        fun peekContent(): T = content

    }

}