package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.*

class PlayerSeekMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return actions.transform {
            if (it is Action.FindPosition) {
                val position = it.position
                if (it.isScrubbing) {
                    mediaPlayer.pause()
                    emit(Result.TrackScrubbing(position) as Result)
                } else {
                    emit(Result.TrackScrubbing(null) as Result)
                    mediaPlayer.seekTo(position)
                    mediaPlayer.play()
                }
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}