package io.radio.shared.feature.player.middleware

import io.radio.shared.base.Logger
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.domain.date.DateProvider
import io.radio.shared.domain.player.PLAYER_SEEK_STEP
import io.radio.shared.domain.player.PlayerSideEffect
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.PlayerAction
import io.radio.shared.feature.player.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform
import kotlin.time.Duration

class PlayerSlipMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val dateProvider: DateProvider
) : Middleware<PlayerAction, PlayerState, PlayerSideEffect> {

    private var clicks = 0
    private var isForward: Boolean? = null
    private var lastEventTime: Duration? = null

    override fun dispatch(
        actions: Flow<PlayerAction>,
        states: StateFlow<PlayerState>
    ): Flow<PlayerAction> {
        return actions.filter {
            it is PlayerAction.SlipForwardClicked || it is PlayerAction.SlipRewindClicked
        }.transform { action ->
            val isForward = action is PlayerAction.SlipForwardClicked
            checkDirection(isForward)
            val currentEventTime = dateProvider.currentTime
            val lastEventTime = this@PlayerSlipMiddleware.lastEventTime
            this@PlayerSlipMiddleware.lastEventTime = currentEventTime

            val hasLastEvent = lastEventTime != null
            val inTime = if (hasLastEvent) {
                currentEventTime.inMilliseconds - lastEventTime!!.inMilliseconds <= DOUBLE_TAP_TIMEOUT
            } else {
                false
            }

            if (!inTime) {
                clicks = 0
            }

            ++clicks

            Logger.d("Clicks $clicks, event time ${currentEventTime.inMilliseconds}, inTime $inTime")

            if (clicks > 1 && inTime) {
                mediaPlayer.slip(if (isForward) STEP else STEP.unaryMinus())
                val slipDurationFormatted = (STEP * (clicks - 1)).let { dateProvider.formatSec(it) }

                emit(
                    if (isForward) {
                        PlayerAction.PlaybackPostForward(slipDurationFormatted)
                    } else {
                        PlayerAction.PlaybackPostRewind(slipDurationFormatted)
                    }
                )
            }
        }
    }

    private fun checkDirection(isForward: Boolean) {
        if (isForward != this.isForward) {
            clicks = 0
        }
        this.isForward = isForward
    }

    private companion object {
        val STEP = PLAYER_SEEK_STEP
        const val DOUBLE_TAP_TIMEOUT = 700
    }

}