package io.radio.presentation.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.radio.shared.model.RadioPodcast
import io.radio.shared.presentation.State
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository
import javax.inject.Inject

class PodcastsViewModel @Inject constructor(private val radioStationRepository: RadioStationRepository) :
    BaseViewModel() {

    private val podcastsMutableLiveData = MutableLiveData<State<List<RadioPodcast>>>()
    val podcastsLiveData: LiveData<State<List<RadioPodcast>>> get() = podcastsMutableLiveData

    init {
        podcastsMutableLiveData.launch { radioStationRepository.getPodcasts() }
    }

}