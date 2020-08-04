@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "UNCHECKED_CAST")

package io.radio.shared.base.mvi

import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import io.radio.shared.base.viewmodel.ViewBinderHelper
import io.radio.shared.presentation.UiCoroutineHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


interface Binder<MviView : Any> {
    suspend fun bind(view: MviView)
}

inline infix fun <MviView : Any> UiCoroutineHolder.bind(binder: Binder<MviView>) {
    viewScope.launch { binder.bind(this@bind as MviView) }
}

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

    infix fun <Event : Persistable> Flow<Event>.bindTo(viewEffects: ViewEffects<Event>)

    /**
     * Creates a binding between this [Flow] and the provided [Store]
     *
     * @receiver a stream of the [Store] `States`
     * @param store a [Store] that will consume the `Intents`
     */
    infix fun <Action : Any> Flow<Action>.bindTo(store: Store<Action, *, *>)

    infix fun <T> Flow<T>.bindTo(stateFlow: MutableStateFlow<T>)

    infix fun <Model : Persistable, Effect : Persistable> ViewBinderHelper<Model, Effect>.bindTo(
        view: MviView<*, Model, Effect>
    )
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

    override fun <Event : Persistable> Flow<Event>.bindTo(viewEffects: ViewEffects<Event>) {
        this bindTo {
            viewEffects.acceptEffect(it)
        }
    }

    override fun <T : Any> Flow<T>.bindTo(store: Store<T, *, *>) {
        this bindTo { store.dispatchAction(it) }
    }

    override fun <T> Flow<T>.bindTo(stateFlow: MutableStateFlow<T>) {
        this bindTo { stateFlow.value = it }
    }

    override fun <Model : Persistable, Effect : Persistable> ViewBinderHelper<Model, Effect>.bindTo(
        view: MviView<*, Model, Effect>
    ) {
        modelFlow bindTo { view.render(it) }
        effectFlow bindTo { view.acceptEffect(it) }
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
