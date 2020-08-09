package io.shared.store.player.middleware

import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlayerStore.*
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