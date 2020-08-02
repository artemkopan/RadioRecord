package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.MediaState
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.*

class PlayerObserveStateMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return mediaPlayer.stateFlow.map {
            when (it) {
                MediaState.Idle -> Result.PlaybackIdle
                MediaState.Buffering -> Result.PlaybackBuffering
                MediaState.Play -> Result.PlaybackPlay
                MediaState.Pause -> Result.PlaybackPause
                MediaState.Ended -> Result.PlaybackEnded
                is MediaState.Error -> Result.PlaybackError(it.throwable)
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}