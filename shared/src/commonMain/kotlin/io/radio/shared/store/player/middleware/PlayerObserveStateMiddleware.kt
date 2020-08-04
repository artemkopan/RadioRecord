package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlaybackState
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

class PlayerObserveStateMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return mediaPlayer.playbackStateFlow.map {
            when (it) {
                PlaybackState.Idle -> Result.PlaybackIdle
                PlaybackState.Buffering -> Result.PlaybackBuffering
                PlaybackState.Play -> Result.PlaybackPlay
                PlaybackState.Pause -> Result.PlaybackPause
                PlaybackState.Ended -> Result.PlaybackEnded
                is PlaybackState.Error -> Result.PlaybackError(it.throwable)
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}