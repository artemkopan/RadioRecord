package io.shared.mvi

import io.shared.core.Persistable
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class AbsMviView<Intent : Any, Model : Persistable, Event : Persistable> :
    MviView<Intent, Model, Event> {

    private val intentsChannel = BroadcastChannel<Intent>(1)

    override val intents: Flow<Intent>
        get() = intentsChannel.asFlow()

    fun dispatchIntent(intent: Intent) {
        intentsChannel.offer(intent)
    }

}