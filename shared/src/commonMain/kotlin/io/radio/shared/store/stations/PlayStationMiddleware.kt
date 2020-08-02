package io.radio.shared.store.stations

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.mapper.TrackItemFromRadioStationMapper
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.stations.StationStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class PlayStationMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val stationMapper: TrackItemFromRadioStationMapper
) : Middleware<Action, Result, State> {
    override fun accept(actions: Flow<Action>, state: StateFlow<State>): Flow<Result> {
        return actions.transform {
            if (it is Action.PlayStation) {
                val track = stationMapper.map(it.station)
                mediaPlayer.prepare(track, null, true)
            }
        }
    }
}