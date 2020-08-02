package io.radio.shared.presentation.podcast.home

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.MviView
import io.radio.shared.base.mvi.ViewEvent
import io.radio.shared.model.Podcast
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.presentation.podcast.home.PodcastView.*

interface PodcastView : MviView<Intent, Model, Event> {

    sealed class Intent {

        data class SelectPodcast(val podcast: Podcast) : Intent()

    }

    data class Model(val isLoading: Boolean, val data: List<Podcast>) : Persistable

    sealed class Event : ViewEvent {

        data class Error(val message: String, override val tag: String) : Event()

        data class NavigateToDetails(val params: PodcastDetailsParams) : Event() {
            override val tag: String
                get() = "Navigate to details"
        }
    }

}