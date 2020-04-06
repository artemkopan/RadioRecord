@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.radio.shared.domain.usecases.track

import io.radio.shared.base.*
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.time.milliseconds

class TrackMediaInfoProcessUseCase(
    private val playerController: PlayerController
) : UseCase<TrackMediaInfoProcessParams, Unit> {


    override suspend fun execute(params: TrackMediaInfoProcessParams) {
        withContext(IoDispatcher) execution@{
            val currentTrackOpt = playerController.observeTrackInfo().first()
            val track = params.track
            if (currentTrackOpt.isEmpty()) {
                Logger.d(
                    TAG,
                    "The player controller does not have any tracks...prepare new: [$track]"
                )
                playerController.prepare(track, params.createPlaylist(), params.autoPlay)
                return@execution
            }

            val currentTrack = currentTrackOpt.getOrThrow()
            if (currentTrack.track.id != track.id) {
                Logger.d(
                    TAG,
                    "The player controller has another track...release current [$currentTrack] and prepare new [$track]"
                )
                playerController.release()
                playerController.prepare(track, params.createPlaylist(), params.autoPlay)
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
                    params.createPlaylist(),
                    params.autoPlay
                )
                else -> throw NotImplementedError("The ${currentTrack.state} is not implemented")
            }
        }
    }

    private fun TrackMediaInfoProcessParams.createPlaylist(): Playlist {
        val position = tracks.indexOf(track)
        require(tracks.isEmpty() || position >= 0) { "Current track is not included in the playlist" }
        return Playlist(tracks, position)
    }

    companion object {
        private const val TAG = "TrackMediaInfoProcessUseCase"
    }

}

data class TrackMediaInfoProcessParams(
    val track: TrackItem,
    val tracks: List<TrackItem> = emptyList(),
    val autoPlay: Boolean = true,
    val justPrepare: Boolean = false
)