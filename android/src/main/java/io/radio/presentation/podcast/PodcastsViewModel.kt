@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.radio.presentation.podcast

import io.radio.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.common.IoDispatcher
import io.radio.shared.model.RadioPodcast
import io.radio.shared.presentation.Event
import io.radio.shared.presentation.State
import io.radio.shared.presentation.toEvent
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PodcastsViewModel @Inject constructor(private val radioStationRepository: RadioStationRepository) :
    BaseViewModel() {

    private val podcastsChannel = ConflatedBroadcastChannel<State<List<RadioPodcast>>>()
    val podcastsFlow: Flow<State<List<RadioPodcast>>> get() = podcastsChannel.asFlow()

    private val openPodcastChannel = ConflatedBroadcastChannel<State<Event<PodcastDetailsParams>>>()
    val openPodcastFlow: Flow<State<Event<PodcastDetailsParams>>> get() = openPodcastChannel.asFlow()

    init {
        podcastsChannel.perform { radioStationRepository.getPodcasts() }
    }

    fun onPodcastSelected(podcast: RadioPodcast) {
        openPodcastChannel.perform {
            withContext(IoDispatcher) {
//todo add later
                PodcastDetailsParams(
                    podcast.id,
                    podcast.cover,
                    0,
                    0
                ).toEvent()
            }
        }
    }


}