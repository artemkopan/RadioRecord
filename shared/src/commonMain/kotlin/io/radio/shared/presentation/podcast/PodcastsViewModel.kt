package io.radio.shared.presentation.podcast

import io.radio.shared.base.Event
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.State
import io.radio.shared.base.toEvent
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.domain.image.ImageLightness
import io.radio.shared.domain.image.ImageProcessor
import io.radio.shared.domain.repositories.station.RadioRepository
import io.radio.shared.domain.resources.AppResources
import io.radio.shared.model.RadioPodcast
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

class PodcastsViewModel constructor(
    private val radioRepository: RadioRepository,
    private val imageProcessor: ImageProcessor,
    private val appResources: AppResources
) :
    ViewModel() {

    private val podcastsChannel = ConflatedBroadcastChannel<State<List<RadioPodcast>>>()
    val podcastsFlow: Flow<State<List<RadioPodcast>>> get() = podcastsChannel.asFlow()

    private val openPodcastChannel = ConflatedBroadcastChannel<State<Event<PodcastDetailsParams>>>()
    val openPodcastFlow: Flow<State<Event<PodcastDetailsParams>>> get() = openPodcastChannel.asFlow()

    init {
        podcastsChannel.perform { radioRepository.getPodcasts() }
    }

    fun onPodcastSelected(podcast: RadioPodcast) {
        openPodcastChannel.perform {
            withContext(IoDispatcher) {
                val image = imageProcessor.getImage(podcast.cover, 100, 100)
                val palette = imageProcessor.generatePalette(image)
                val isDark = imageProcessor.getLightness(palette) == ImageLightness.Dark

                PodcastDetailsParams(
                    podcast.id,
                    podcast.name,
                    podcast.cover,
                    imageProcessor.getDarkerColor(
                        imageProcessor.getDominantColor(
                            palette,
                            appResources.accentColor
                        )
                    ),
                    if (isDark) {
                        appResources.primaryColor
                    } else {
                        appResources.accentColor
                    }
                ).toEvent()
            }
        }
    }

}