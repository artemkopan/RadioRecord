package io.radio.shared.feature.tracks

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.MediaState
import io.radio.shared.formatters.TrackFormatter
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackItemWithMediaState
import kotlinx.coroutines.flow.*

class ObserveTracksStateMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val trackFormatter: TrackFormatter
) : Middleware2<ObserveTracksStateResult> {

    override fun dispatch(actionFlow: Flow<Action>): Flow<ObserveTracksStateResult> {
        return actionFlow.transformLatest {
            if (it !is ObserveTracksStateAction) {
                return@transformLatest
            }
            emitAll(mediaPlayer.trackFlow.combineTransform(mediaPlayer.stateFlow) { trackOpt, state ->
                val tracks = it.tracks.map { track ->
                    val isPreparedInPlayer = track.id == trackOpt.data?.id
                    TrackItemWithMediaState(
                        track,
                        if (isPreparedInPlayer) state else MediaState.Idle,
                        trackFormatter.formatDuration(track.duration)
                    )
                }
                emit(ObserveTracksStateResult.Success(tracks) as ObserveTracksStateResult)
            })
        }.flowOn(IoDispatcher).catch { emit(ObserveTracksStateResult.Error(it)) }
    }
}

data class ObserveTracksStateAction(val tracks: List<TrackItem>) : Action

sealed class ObserveTracksStateResult : Result {

    data class Error(val throwable: Throwable) : ObserveTracksStateResult()
    data class Success(val tracks: List<TrackItemWithMediaState>) : ObserveTracksStateResult()

}