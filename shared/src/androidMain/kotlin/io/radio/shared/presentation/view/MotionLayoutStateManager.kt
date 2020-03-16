@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.radio.shared.presentation.view

import androidx.annotation.IdRes
import io.radio.shared.presentation.view.MotionCommand.*
import io.radio.shared.presentation.view.MotionCommand.Set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MotionLayoutStateManager(
    private val scope: CoroutineScope,
    motionLayout: MultiListenerMotionLayout
) {

    private val states = BroadcastChannel<MotionCommand>(1)

    init {
        scope.launch {
            states.openSubscription().consumeEach {
                when (it) {
                    is Await -> motionLayout.awaitTransitionComplete(it.transitionId)
                    is Set -> motionLayout.setTransition(it.beginId, it.endId)
                    is ToState -> motionLayout.transitionToState(it.stateId)
                }
            }
        }
    }

    fun send(command: MotionCommand) {
        scope.launch {
            states.send(command)
        }
    }

    fun send(vararg command: MotionCommand) {
        scope.launch {
            command.forEach { states.send(it) }
        }
    }

}

sealed class MotionCommand {

    class Await(@IdRes val transitionId: Int) : MotionCommand()
    class Set(@IdRes val beginId: Int, @IdRes val endId: Int) : MotionCommand()
    class ToState(@IdRes val stateId: Int) : MotionCommand()
}
