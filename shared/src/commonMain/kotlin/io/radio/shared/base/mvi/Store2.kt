package io.radio.shared.base.mvi

import io.radio.shared.base.Logger
import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class Store2Impl<Intent, SideEffect, State : Persistable, Signal : Persistable>(
    private val scope: CoroutineScope,
    private val middlewareList: List<Middleware2<out Result>>,
    initialState: State
) {

    private val stateFlow = MutableStateFlow(initialState)
    private val signalFlow = MutableStateFlow<Signal?>(null)
    private val intents = BroadcastChannel<Intent>(1)
    private val sideEffects = BroadcastChannel<SideEffect>(1)

    init {
        wire()
    }

    abstract val intentMapper: Intent.() -> Action
    abstract val sideEffectMapper: SideEffect.() -> Action
    abstract val reducer: Result.(State) -> State

    fun bind(mviView: MviView2<Intent, State, Signal>) {
        scope.launch {
            stateFlow.onEach {
                withContext(MainDispatcher) { mviView.render(it) }
            }.launchIn(this)
            signalFlow.onEach {
                withContext(MainDispatcher) { it?.let { mviView.applySignal(it) } }
            }.launchIn(this)
            mviView.intents.onEach { intents.send(it) }.launchIn(this)
        }
    }

    protected open fun dispatchSignal(signal: Signal) {
        Logger.d(TAG, "dispatchSignal() called with: signal = $signal")
        scope.launch { signalFlow.value = signal }
    }

    protected open fun dispatchSideEffect(sideEffect: SideEffect) {
        Logger.d(TAG, "dispatchSideEffect() called with: sideEffect = $sideEffect")
        scope.launch { sideEffects.send(sideEffect) }
    }

    protected fun throwNotImplemented(result: Result): State {
        throw NotImplementedError("Not implemented result $result")
    }

    private fun wire() {
        scope.launch {
            val actions = merge(
                intents.asFlow().map { intentMapper(it) },
                sideEffects.asFlow().map { sideEffectMapper(it) }
            )
            middlewareList.map { it.dispatch(actions) }
                .merge()
                .onEach { result ->
                    val currentState = stateFlow.value
                    stateFlow.value = reducer(result, currentState).also {
                        Logger.d(TAG, "State changed, old: $currentState, new: $it")
                    }
                }
                .launchIn(this)
        }
    }

    companion object {
        private const val TAG = "Store"
    }
}

