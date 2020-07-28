package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.middleware.LoggingMiddleware
import io.radio.shared.base.mvi.middleware.PersistStateMiddleware
import io.radio.shared.base.mvi.v3.Store
import io.radio.shared.base.mvi.v3.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.CoroutineScope

data class StoreBuilder<A, S : Persistable, E : Persistable>(
    private var reducer: Reducer<S, A, E>? = null,
    private var initialState: S? = null,
    private var middlewareList: List<Middleware<A, S>> = emptyList(),
    private var restoreStateIfPossible: Boolean = true
) {

    private var additionalMiddlewares = mutableListOf<Middleware<A, S>>()
    private var stateStorage: StateStorage? = null
    private var stateTag: String = ""

    fun middlewareList(middlewareList: List<Middleware<A, S>>) =
        apply { this.middlewareList = middlewareList }

    fun enableLogging() = apply { additionalMiddlewares.add(LoggingMiddleware()) }

    fun enablePersistState(tag: String, stateStorage: StateStorage) = apply {
        additionalMiddlewares.add(
            PersistStateMiddleware(
                tag,
                stateStorage
            )
        )
        this.stateTag = tag
        this.stateStorage = stateStorage
    }

    fun reducer(reducer: Reducer<S, A, E>) = apply { this.reducer = reducer }

    fun initialState(initialState: S) = apply { this.initialState = initialState }

    fun restoreStateIfPossible(restoreStateIfPossible: Boolean) = apply {
        this.restoreStateIfPossible = restoreStateIfPossible
    }

    fun build(scope: CoroutineScope): Store<A, S, E> {
        val stateStorage = stateStorage
        return StoreImpl(
            requireNotNull(reducer) { "Reducer must be initialized" },
            additionalMiddlewares.plus(middlewareList),
            stateStorage.takeIf { restoreStateIfPossible && stateStorage != null }
                ?.get<S>(stateTag)
                ?: requireNotNull(initialState) { "Initial state must be initialized" },
            scope
        )
    }

}