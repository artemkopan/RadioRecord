package io.radio.shared.store.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PlayerChangeTrackMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result> {
        return actionFlow.transform {
            if (it is Action.PlayNext && state().isNextAvailable) {
                mediaPlayer.next()
            } else if (it is Action.PlayPrevious && state().isPreviousAvailable) {
                mediaPlayer.previous()
            }
        }
    }
}