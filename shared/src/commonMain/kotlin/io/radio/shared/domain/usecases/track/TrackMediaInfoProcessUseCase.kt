@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.radio.shared.domain.usecases.track

import io.radio.shared.base.*
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.time.milliseconds

class TrackMediaInfoProcessUseCase(
    private val playerController: PlayerController
) : UseCase<TrackItem, Unit>, UseCaseBiParams<TrackItem, TrackMediaInfoProcessParams, Unit> {

    override suspend fun execute(track: TrackItem) {
        execute(track, TrackMediaInfoProcessParams())
    }

    override suspend fun execute(track: TrackItem, params: TrackMediaInfoProcessParams) =
        withContext(IoDispatcher) execution@{
            val trackOpt = playerController.observeTrackInfo().first()
            if (trackOpt.isEmpty()) {
                Logger.d(
                    TAG,
                    "The player controller does not have any tracks...prepare new: [$track]"
                )
                playerController.prepare(track, params.autoPlay)
                return@execution
            }

            val currentTrack = trackOpt.getOrThrow()
            if (currentTrack.track.id != track.id) {
                Logger.d(
                    TAG,
                    "The player controller has another track...release current [$currentTrack] and prepare new [$track]"
                )
                playerController.release()
                playerController.prepare(track, params.autoPlay)
                return@execution
            }

            Logger.d(TAG, "Current track state: ${currentTrack.state}")

            when (currentTrack.state) {
                TrackMediaState.None -> throw IllegalArgumentException("Exception in implementation, state can not be none")
                TrackMediaState.Buffering -> {
                    //no-op
                }
                TrackMediaState.Preparing -> return@execution //track is already preparing, skip
                TrackMediaState.Play -> {
                    if (!params.justPrepare) {
                        playerController.pause()
                    }
                }
                TrackMediaState.Pause -> {
                    if (!params.justPrepare) {
                        playerController.play()
                    }
                }
                TrackMediaState.Ended -> {
                    playerController.setPosition(0.0.milliseconds)
                    playerController.play()
                }
                is TrackMediaState.Error -> playerController.prepare(
                    currentTrack.track,
                    params.autoPlay
                )
                else -> throw NotImplementedError("The ${currentTrack.state} is not implemented")
            }
        }

    companion object {
        private const val TAG = "TrackMediaInfoProcessUseCase"
    }

}

data class TrackMediaInfoProcessParams(
    val autoPlay: Boolean = true,
    val justPrepare: Boolean = false
)