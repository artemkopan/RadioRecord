package io.radio.shared.domain.player

import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlayerStatesMediator {

    val playerActionsFlow: Flow<PlayerAction>

    fun postState(state: PlayerState)
    fun postSideEffects(effect: PlayerSideEffect)
}

sealed class PlayerAction {

    data class Preparing(val track: TrackItem) : PlayerAction()
    object Release : PlayerAction()
    object Play : PlayerAction()
    object Pause : PlayerAction()
    data class SetPosition(val position: Duration) : PlayerAction()
    data class SeekTo(val offset: Duration) : PlayerAction()

}

sealed class PlayerState {

    object PlayTrack : PlayerState()
    object PauseTrack : PlayerState()
    object BufferingTrack : PlayerState()
    object EndedTrack: PlayerState()
    data class Error(val throwable: Throwable) : PlayerState()

}

sealed class PlayerSideEffect {

    data class MetaData(val value: PlayerMetaData) : PlayerSideEffect()
    data class StreamMetaData(val title: String): PlayerSideEffect()
    data class TrackPosition(
        val currentPosition: Duration,
        val bufferedPosition: Duration,
        val contentPosition: Duration
    ) : PlayerSideEffect()
    object TrackPositionReset: PlayerSideEffect()
}