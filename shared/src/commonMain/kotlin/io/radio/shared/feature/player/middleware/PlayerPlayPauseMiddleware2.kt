package io.radio.shared.feature.player.middleware

import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.MediaState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PlayerPlayPauseMiddleware2(
    private val mediaPlayer: MediaPlayer
) : Middleware2<Result.Noting> {

    override fun dispatch(actionFlow: Flow<Action>): Flow<Result.Noting> {
        return actionFlow.transform {
            if (it !is PlayerPlayPauseAction) {
                return@transform
            }
            when (mediaPlayer.stateFlow.value) {
                MediaState.Pause -> mediaPlayer.play()
                MediaState.Idle,
                MediaState.Buffering,
                MediaState.Play,
                MediaState.Ended,
                is MediaState.Error -> mediaPlayer.pause()
            }
        }
    }
}

object PlayerPlayPauseAction : Action