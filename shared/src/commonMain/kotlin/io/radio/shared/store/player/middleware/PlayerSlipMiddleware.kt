package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Logger
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.date.DateProvider
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration
import kotlin.time.seconds

class PlayerSlipMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val dateProvider: DateProvider
) : Middleware<Action, Result, State> {

    private var clicks = 0
    private var isForward: Boolean? = null
    private var lastEventTime: Duration? = null

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transformLatest { action ->

            val isForward = when (action) {
                is Action.SlipForward -> true
                is Action.SlipRewind -> false
                else -> return@transformLatest
            }

            checkDirection(isForward)

            val currentEventTime = dateProvider.currentTime

            val lastEventTime = lastEventTime
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
                mediaPlayer.slip(if (isForward) STEP.unaryMinus() else STEP)

                val forwardDuration: Duration?
                val rewindDuration: Duration?

                val slipDuration = (STEP * (clicks - 1))

                if (isForward) {
                    forwardDuration = slipDuration
                    rewindDuration = null
                } else {
                    forwardDuration = null
                    rewindDuration = slipDuration
                }

                emit(Result.PlaybackPostRewind(forwardDuration, rewindDuration))

                delay(DOUBLE_TAP_TIMEOUT)

                //reset
                emit(Result.PlaybackPostRewind(null, null) as Result)
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }

    private fun checkDirection(isForward: Boolean) {
        if (isForward != this.isForward) {
            clicks = 0
        }
        this.isForward = isForward
    }

    private companion object {
        val STEP = 10.seconds
        const val DOUBLE_TAP_TIMEOUT = 700L
    }

}