package io.radio.shared.feature.tracks

import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PlayerTrackSelectionMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware2<PlayerTrackSelectionResult> {

    override fun dispatch(actionFlow: Flow<Action>): Flow<PlayerTrackSelectionResult> {
        return actionFlow.transform {
            if (it !is PlayerTrackSelectionAction) return@transform
            if (it.trackItem == mediaPlayer.trackFlow.value.data) {
                emit(PlayerTrackSelectionResult.TrackAlreadyPrepared)
            } else {
                mediaPlayer.prepare(
                    it.trackItem,
                    it.playlist,
                    it.autoPlay
                )
            }
        }
    }
}


data class PlayerTrackSelectionAction(
    val trackItem: TrackItem,
    val playlist: Playlist,
    val autoPlay: Boolean
) : Action


sealed class PlayerTrackSelectionResult : Result {

    object TrackAlreadyPrepared : PlayerTrackSelectionResult()

}