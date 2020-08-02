package io.radio.shared.base.mvi

import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.supervisorScope


/**
 * A builder function for the [Binder]
 *
 * @param builder the DSL block function
 *
 * @return a new instance of the [Binder]
 */
suspend fun bind(
    builder: BindingsBuilder.() -> Unit
) = supervisorScope {
    BuilderBinder(this).also(builder).start()
}

interface BindingsBuilder {

    /**
     * Creates a binding between this [Flow] and the provided `consumer`
     *
     * @receiver a stream of values
     * @param consumer a `consumer` of values
     */
    infix fun <T> Flow<T>.bindTo(consumer: suspend (T) -> Unit)

    /**
     * Creates a binding between this [Flow] and the provided [ViewRenderer]
     *
     * @receiver a stream of the `View Models`
     * @param viewRenderer a [ViewRenderer] that will consume the `View Models`
     */
    infix fun <Model : Persistable> Flow<Model>.bindTo(viewRenderer: ViewRenderer<Model>)

    infix fun <Event : Persistable> Flow<Event>.bindTo(viewEvents: ViewEvents<Event>)

    /**
     * Creates a binding between this [Flow] and the provided [Store]
     *
     * @receiver a stream of the [Store] `States`
     * @param store a [Store] that will consume the `Intents`
     */
    infix fun <Action : Any> Flow<Action>.bindTo(store: Store<Action, *, *>)
}

@OptIn(InternalCoroutinesApi::class)
private class BuilderBinder(
    private val scope: CoroutineScope
) : BindingsBuilder {

    private val bindings = ArrayList<Binding<*>>()

    override fun <T> Flow<T>.bindTo(consumer: suspend (T) -> Unit) {
        bindings += Binding(this, consumer)
    }

    override fun <Model : Persistable> Flow<Model>.bindTo(viewRenderer: ViewRenderer<Model>) {
        this bindTo {
            viewRenderer.render(it)
        }
    }

    override fun <Event : Persistable> Flow<Event>.bindTo(viewEvents: ViewEvents<Event>) {
        this bindTo {
            viewEvents.acceptEvent(it)
        }
    }

    override fun <T : Any> Flow<T>.bindTo(store: Store<T, *, *>) {
        this bindTo { store.dispatchAction(it) }
    }

    fun start() {
        bindings.forEach { binding -> startBinding(binding) }
    }

    private fun <T> startBinding(binding: Binding<T>) {
        binding.source
            .onEach { binding.consumer(it) }
            .flowOn(MainDispatcher)
            .launchIn(scope)
    }

}

private class Binding<T>(
    val source: Flow<T>,
    val consumer: suspend (T) -> Unit
)
