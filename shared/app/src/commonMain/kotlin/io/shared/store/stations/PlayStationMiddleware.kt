package io.shared.store.stations

import io.shared.mapper.TrackItemFromRadioStationMapper
import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.stations.StationStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest

class PlayStationMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val stationMapper: TrackItemFromRadioStationMapper
) : Middleware<Action, Result, State> {

    override fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result> {
        return actionFlow.transformLatest { action ->
            if (action is Action.PlayStation) {
                val track = stationMapper.map(action.station)
                if (mediaPlayer.trackFlow.value.data != track) {
                    mediaPlayer.prepare(track, null, true)
                }
                emitAll(mediaPlayer.trackFlow.transform {
                    emit(
                        Result.PlayingStation(
                            if (track == it.data) {
                                action.station
                            } else {
                                null
                            }
                        )
                    )
                })
            }
        }
    }

}