package io.radio.shared.presentation.stations

import io.radio.shared.base.Event
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.State
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.data.mapper.TrackItemFromRadioStationMapper
import io.radio.shared.domain.repositories.station.RadioRepository
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessParams
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.model.RadioStation
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

class StationsViewModel constructor(
    private val radioRepository: RadioRepository,
    private val trackItemFromRadioStationMapper: TrackItemFromRadioStationMapper,
    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase
) :
    ViewModel() {

    private val stationsChannel = ConflatedBroadcastChannel<State<List<RadioStation>>>()
    val stationsFlow: Flow<State<List<RadioStation>>> get() = stationsChannel.asFlow()

    private val openStationStateChannel = ConflatedBroadcastChannel<State<Event<Unit>>>()
    val openStationStateFlow: Flow<State<Event<Unit>>> get() = openStationStateChannel.asFlow()

    init {
        stationsChannel.perform { radioRepository.getStations() }
    }

    fun onStationClicked(item: RadioStation) {
        openStationStateChannel.perform {
            withContext(IoDispatcher) {
                val track = trackItemFromRadioStationMapper.map(item)
                trackMediaInfoProcessUseCase.execute(
                    TrackMediaInfoProcessParams(
                        track,
                        justPrepare = true
                    )
                )
            }
            Event(Unit)
        }
    }

}

