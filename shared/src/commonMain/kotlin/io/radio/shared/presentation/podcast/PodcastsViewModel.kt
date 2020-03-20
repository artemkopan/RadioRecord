@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.radio.shared.presentation.podcast

import io.radio.shared.common.*
import io.radio.shared.common.viewmodel.ViewModel
import io.radio.shared.model.RadioPodcast
import io.radio.shared.repositories.station.RadioStationRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

class PodcastsViewModel @Inject constructor(private val radioStationRepository: RadioStationRepository) :
    ViewModel() {

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