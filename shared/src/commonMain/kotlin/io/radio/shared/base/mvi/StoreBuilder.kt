package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.impl.LoggingMiddleware
import io.radio.shared.base.mvi.impl.PersistStateMiddleware
import io.radio.shared.base.viewmodel.StateStorage

data class StoreBuilder<A, S : Persistable, E>(
    private var reducer: Reducer<S, A>? = null,
    private var initialState: S? = null,
    private var middlewareList: List<Middleware<A, S, E>> = emptyList(),
    private var restoreStateIfPossible: Boolean = true
) {

    private var additionalMiddlewares = mutableListOf<Middleware<A, S, E>>()
    private var stateStorage: StateStorage? = null
    private var stateTag: String = ""

    fun middlewareList(middlewareList: List<Middleware<A, S, E>>) =
        apply { this.middlewareList = middlewareList }

    fun enableLogging() = apply { additionalMiddlewares.add(LoggingMiddleware()) }

    fun enablePersistState(tag: String, stateStorage: StateStorage) = apply {
        additionalMiddlewares.add(PersistStateMiddleware(tag, stateStorage))
        this.stateTag = tag
        this.stateStorage = stateStorage
    }

    fun reducer(reducer: Reducer<S, A>) = apply { this.reducer = reducer }

    fun initialState(initialState: S) = apply { this.initialState = initialState }

    fun restoreStateIfPossible(restoreStateIfPossible: Boolean) = apply {
        this.restoreStateIfPossible = restoreStateIfPossible
    }

    fun build(): Store<A, S, E> {
        val stateStorage = stateStorage
        return StoreImpl(
            requireNotNull(reducer) { "Reducer must be initialized" },
            additionalMiddlewares.plus(middlewareList),
            stateStorage.takeIf { restoreStateIfPossible && stateStorage != null }
                ?.get<S>(stateTag)
                ?: requireNotNull(initialState) { "Initial state must be initialized" }
        )
    }

}