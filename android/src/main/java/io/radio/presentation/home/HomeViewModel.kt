package io.radio.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.radio.shared.common.Inject
import io.radio.shared.model.RadioPodcast
import io.radio.shared.model.RadioStation
import io.radio.shared.presentation.State
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository

class HomeViewModel @Inject constructor(
    radioStationRepository: RadioStationRepository
) : BaseViewModel() {

    private val stationsMutableLiveData = MutableLiveData<State<List<RadioStation>>>()
    val stationLiveData: LiveData<State<List<RadioStation>>> get() = stationsMutableLiveData

    private val podcastsMutableLiveData = MutableLiveData<State<List<RadioPodcast>>>()
    val podcastsLiveData: LiveData<State<List<RadioPodcast>>> get() = podcastsMutableLiveData

    init {
        stationsMutableLiveData.launch { radioStationRepository.getStations() }
        podcastsMutableLiveData.launch { radioStationRepository.getPodcasts() }
    }


}