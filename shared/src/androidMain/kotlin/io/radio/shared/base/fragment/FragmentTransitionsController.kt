package io.radio.shared.base.fragment

import android.os.Bundle
import android.transition.Transition
import androidx.core.transition.addListener
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class FragmentTransitionsController(private val fragment: BaseFragment) {

    private val animationEnded = ConflatedBroadcastChannel<Unit>()
    private val defaultTransitionDuration: Long = DEFAULT_TRANSITION_LENGTH
    private var transitionDuration: Long = DEFAULT_TRANSITION_LENGTH
    private var wasShowed = false

    init {
        fragment.savedStateRegistry.registerSavedStateProvider(KEY_PROVIDER) {
            Bundle(1).apply { putBoolean(KEY_SHOW, wasShowed) }
        }
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                wasShowed = fragment.savedStateRegistry.consumeRestoredStateForKey(KEY_PROVIDER)
                    ?.getBoolean(KEY_SHOW) ?: false
            }
        })
    }

    fun registerSharedEnterElementTransition() {
        val transition = fragment.sharedElementEnterTransition as Transition
        updateDuration(transition)
        val onEnd: (transition: Transition) -> Unit = {
            animationEnded.sendBlocking(Unit)
        }
        transition.addListener(onEnd = onEnd, onCancel = onEnd)
    }

    fun awaitTransitionAnimationComplete(onComplete: () -> Unit) {
        if (!wasShowed) {
            fragment.viewScope.launch {
                withTimeoutOrNull(transitionDuration) { animationEnded.asFlow().single() }
                wasShowed = true
                onComplete()
            }
        } else {
            onComplete()
        }
    }

    private fun updateDuration(transition: Transition) {
        transitionDuration = if (transition.duration > 0) {
            transition.duration
        } else {
            defaultTransitionDuration
        }
    }


    companion object {
        private const val KEY_PROVIDER = "FragmentTransitionsController"
        private const val KEY_SHOW = "key-is-showed"
        private const val DEFAULT_TRANSITION_LENGTH = 500L
    }

}