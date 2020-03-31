@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.radio.shared.domain.usecases.track

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.UseCasesBiParams
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.player.PlayerController
import kotlinx.coroutines.withContext
import kotlin.time.seconds

class TrackUpdatePositionUseCase(
    private val trackFormatter: TrackFormatter,
    private val playerController: PlayerController
) : UseCasesBiParams<Int, Boolean, String> {

    override suspend fun execute(positionSec: Int, isScrubbing: Boolean): String =
        withContext(IoDispatcher) {
            val position = positionSec.seconds
            if (isScrubbing) {
                playerController.pause()
            } else {
                playerController.setPosition(position)
                playerController.play()
            }
            trackFormatter.formatDuration(position)
        }
}