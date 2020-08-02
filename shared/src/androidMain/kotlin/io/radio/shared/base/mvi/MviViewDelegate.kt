@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package io.radio.shared.base.mvi

import androidx.core.os.bundleOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import io.radio.shared.base.Logger
import io.radio.shared.base.Persistable
import kotlinx.coroutines.flow.Flow


inline fun <Intent : Any, Model : Persistable, Event : ViewEvent> mviViewDelegate(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    noinline intentFlow: () -> Flow<Intent>,
    noinline onRender: Model.() -> Unit,
    noinline onEvent: Event.() -> Unit = {}
) =
    MviViewDelegate(savedStateRegistryOwner, intentFlow, onRender, onEvent)


open class MviViewDelegate<Intent : Any, Model : Persistable, Event : ViewEvent>(
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val intentFlow: () -> Flow<Intent>,
    private val onRender: Model.() -> Unit,
    private val onEvent: Event.() -> Unit = {}
) : MviView<Intent, Model, Event> {

    private var events = mutableMapOf<String, Event>()

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            consumeEventsIfExist()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
        }
    }

    init {
        if (savedStateRegistryOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            consumeEventsIfExist()
        }
        savedStateRegistryOwner.savedStateRegistry.unregisterSavedStateProvider(KEY)
        savedStateRegistryOwner.savedStateRegistry.registerSavedStateProvider(KEY) {
            bundleOf(ARG_EVENT to events)
        }

        savedStateRegistryOwner.lifecycle.addObserver(lifecycleObserver)
    }

    override val intents: Flow<Intent>
        get() = intentFlow()

    override fun render(model: Model) {
        onRender(model)
    }

    /**
     * each event must have unique tag for displaying in UI
     */
    override fun acceptEvent(event: Event) {
        Logger.d("MviViewDelegate", "Accept event: $event")
        if (events[event.tag] != event) {

            events.putIfAbsent(event.tag, event)
            onEvent(event)
        }
    }

    private fun consumeEventsIfExist() {
        val event = savedStateRegistryOwner.savedStateRegistry
            .consumeRestoredStateForKey(KEY)
            ?.getSerializable(ARG_EVENT)
        if (event != null && events.isEmpty()) {
            events = event as HashMap<String, Event>
        }
    }

    companion object {
        private const val KEY = "mvi-view-interceptor"
        private const val ARG_EVENT = "mvi-view-event"
    }

}