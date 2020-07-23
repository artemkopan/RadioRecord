package io.radio.shared.feature.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.domain.player.PlayerSideEffect
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.PlayerAction
import io.radio.shared.feature.player.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform

class PlayerPlayPauseMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<PlayerAction, PlayerState, PlayerSideEffect> {

    override fun dispatch(
        actions: Flow<PlayerAction>,
        states: StateFlow<PlayerState>
    ): Flow<PlayerAction> {
        return actions.filter {
            it is PlayerAction.PlayPauseClicked
        }.transform {
            if (states.value.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.play()
            }
        }
    }
}