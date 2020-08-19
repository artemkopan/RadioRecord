package io.shared.mvi

import io.shared.core.MainDispatcher
import io.shared.core.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

abstract class AbsMviView<Intent : Any, Model : Persistable, Effect : Persistable> :
    MviView<Intent, Model, Effect> {

    private val intentsChannel = BroadcastChannel<Intent>(1)

    override val intents: Flow<Intent>
        get() = intentsChannel.asFlow()

    fun dispatchIntent(intent: Intent) {
        intentsChannel.offer(intent)
    }

    fun attachBinder(viewBinder: Binder<Intent, Model, Effect>): BinderDisposable {
        val job = CoroutineScope(MainDispatcher).launch {
            attachBinder(viewBinder)
        }
        return object : BinderDisposable {
            override fun dispose() {
                job.cancel()
            }
        }
    }

}