@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.radio.shared.domain.usecases.track

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.UseCaseBiParams
import io.radio.shared.domain.player.PlayerController
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.seconds

class TrackUpdatePositionUseCase(
    private val playerController: PlayerController
) : UseCaseBiParams<Int, Boolean, Duration> {

    override suspend fun execute(positionSec: Int, isScrubbing: Boolean): Duration =
        withContext(IoDispatcher) {
            val position = positionSec.seconds
            if (isScrubbing) {
                playerController.pause()
            } else {
                playerController.setPosition(position)
                playerController.play()
            }
            position
        }
}