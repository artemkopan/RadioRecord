package io.radio.presentation.station

import io.radio.shared.common.State
import io.radio.shared.common.viewmodel.ViewModel
import io.radio.shared.model.RadioStation
import io.radio.shared.repositories.station.RadioStationRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class StationsViewModel @Inject constructor(private val radioStationRepository: RadioStationRepository) :
    ViewModel() {

    private val stationsChannel = ConflatedBroadcastChannel<State<List<RadioStation>>>()
    val stationsFlow: Flow<State<List<RadioStation>>> get() = stationsChannel.asFlow()

    init {
        stationsChannel.perform { radioStationRepository.getStations() }
    }

}