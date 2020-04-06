@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.radio.shared.domain.usecases.track

import io.radio.shared.base.Logger
import io.radio.shared.base.Optional
import io.radio.shared.base.UseCase
import io.radio.shared.base.toOptional
import io.radio.shared.domain.date.DateProvider
import io.radio.shared.domain.player.PLAYER_SEEK_STEP
import io.radio.shared.domain.player.PlayerController
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class TrackSeekUseCase(
    private val playerController: PlayerController,
    private val dateProvider: DateProvider
) : UseCase<Boolean, Optional<Duration>> {

    private var clicks = 0
    private var isForward: Boolean? = null
    private var lastEventTime: Duration? = null

    private val mutex = Mutex()

    override suspend fun execute(isForward: Boolean): Optional<Duration> =
        mutex.withLock execution@{
            checkDirection(isForward)
            val currentEventTime = dateProvider.currentTime
            val lastEventTime = this.lastEventTime
            this.lastEventTime = currentEventTime

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

            return@execution if (clicks > 1 && inTime) {
                playerController.seekTo(if (isForward) STEP else STEP.unaryMinus())
                (STEP * (clicks - 1)).toOptional()
            } else {
                Optional.empty()
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