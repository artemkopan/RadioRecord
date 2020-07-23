package io.radio.shared.feature.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.player.PlayerSideEffect
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.PlayerAction
import io.radio.shared.feature.player.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlin.time.seconds

class PlayerSeekMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val trackFormatter: TrackFormatter
) : Middleware<PlayerAction, PlayerState, PlayerSideEffect> {

    override fun dispatch(
        actions: Flow<PlayerAction>,
        states: StateFlow<PlayerState>
    ): Flow<PlayerAction> {
        return actions.transform {
            if (it is PlayerAction.Seek) {
                val position = it.position
                if (it.isScrubbing) {
                    mediaPlayer.pause()
                    emit(PlayerAction.TrackScrubbing(trackFormatter.formatDuration(position.seconds)))
                } else {
                    mediaPlayer.seekTo(position.seconds)
                    mediaPlayer.play()
                }
            }
        }
    }
}