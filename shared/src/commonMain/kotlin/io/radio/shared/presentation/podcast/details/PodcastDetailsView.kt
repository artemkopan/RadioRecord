package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.MviView
import io.radio.shared.base.mvi.ViewEvent
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaStateItem
import io.radio.shared.presentation.podcast.details.PodcastDetailsView.*

interface PodcastDetailsView : MviView<Intent, Model, Event> {

    sealed class Intent {

        data class TrackClick(val trackItem: TrackItem) : Intent()
        data class PlayPauseClick(val trackItem: TrackItem) : Intent()

    }

    data class Model(
        val logo: String = "",
        val title: String = "",
        val tracksWithState: List<TrackMediaStateItem> = emptyList(),
        val playlist: Playlist? = null,
        val headerColor: Int = 0
    ) : Persistable

    sealed class Event : ViewEvent {

        object NavigateToPlayer : Event() {
            override val tag: String
                get() = "NavigateToPlayer"
        }

        data class Error(val message: String, override val tag: String) : Event()

    }

}