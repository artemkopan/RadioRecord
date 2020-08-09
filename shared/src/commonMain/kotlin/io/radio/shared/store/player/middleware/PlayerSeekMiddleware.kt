package io.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlaybackState
import io.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transformLatest

class PlayerSeekMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    private var wasPaused: Boolean? = null

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transformLatest {
            if (it is Action.FindPosition) {
                val position = it.position
                if (it.isScrubbing) {
                    if (wasPaused == null) {
                        //don't change pause state while scrubbing
                        wasPaused = mediaPlayer.playbackStateFlow.value == PlaybackState.Pause
                    }
                    mediaPlayer.pause()
                    emit(Result.TrackScrubbing(position) as Result)
                } else {
                    mediaPlayer.seekTo(position)
                    if (wasPaused == false) {
                        mediaPlayer.play()
                    }
                    wasPaused = null
                    emit(Result.TrackScrubbing(null) as Result)
                }
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}