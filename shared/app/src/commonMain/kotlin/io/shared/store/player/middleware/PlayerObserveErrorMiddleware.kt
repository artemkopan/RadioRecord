package io.shared.store.player.middleware

import io.shared.core.IoDispatcher
import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

class PlayerObserveErrorMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return mediaPlayer.errorFlow
            .map {
                Result.PlaybackError(it.data)
            }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}