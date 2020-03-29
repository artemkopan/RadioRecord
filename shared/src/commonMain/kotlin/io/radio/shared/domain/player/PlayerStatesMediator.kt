package io.radio.shared.domain.player

import io.radio.shared.model.TrackSource
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlayerStatesMediator {

    val playerActionsFlow: Flow<PlayerAction>

    fun postState(state: PlayerState)
    fun postSideEffects(effect: PlayerSideEffect)
}

sealed class PlayerAction {

    data class Preparing(val source: TrackSource) : PlayerAction()
    object Release : PlayerAction()
    object Play : PlayerAction()
    object Pause : PlayerAction()
    data class SetPosition(val positionMs: Long) : PlayerAction()
    data class SeekTo(val offsetMs: Long) : PlayerAction()

}

sealed class PlayerState {

    object PlayTrack : PlayerState()
    object PauseTrack : PlayerState()
    object BufferingTrack : PlayerState()
    data class Error(val throwable: Throwable) : PlayerState()

}

sealed class PlayerSideEffect {

    data class MetaData(val value: PlayerMetaData) : PlayerSideEffect()
    data class TrackPosition(
        val currentPosition: Duration,
        val bufferedPosition: Duration,
        val contentPosition: Duration
    ) : PlayerSideEffect()

}