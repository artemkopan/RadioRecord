package io.radio.presentation.station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.radio.shared.model.RadioStation
import io.radio.shared.presentation.State
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository
import javax.inject.Inject

class StationsViewModel @Inject constructor(private val radioStationRepository: RadioStationRepository) :
    BaseViewModel() {

    private val stationsMutableLiveData = MutableLiveData<State<List<RadioStation>>>()
    val stationLiveData: LiveData<State<List<RadioStation>>> get() = stationsMutableLiveData

    init {
        stationsMutableLiveData.launch { radioStationRepository.getStations() }
    }

}