package io.radio.shared.store.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PlayerPlayPauseMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transform {
            if (it is Action.SwitchPlayback)
                if (state().isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.play()
                }
        }
    }
}