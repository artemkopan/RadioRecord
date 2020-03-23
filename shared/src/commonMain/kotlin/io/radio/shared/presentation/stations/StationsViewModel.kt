package io.radio.shared.presentation.stations

import io.radio.shared.base.State
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.data.repositories.station.RadioRepository
import io.radio.shared.model.RadioStation
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class StationsViewModel constructor(private val radioRepository: RadioRepository) :
    ViewModel() {

    private val stationsChannel = ConflatedBroadcastChannel<State<List<RadioStation>>>()
    val stationsFlow: Flow<State<List<RadioStation>>> get() = stationsChannel.asFlow()

    init {
        stationsChannel.perform { radioRepository.getStations() }
    }

}

