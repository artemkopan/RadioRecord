package io.shared.presentation.podcast.details

import io.shared.core.Persistable
import io.shared.model.Playlist
import io.shared.model.ResourceString
import io.shared.model.TrackItem
import io.shared.model.TrackPlaybackStateItem
import io.shared.mvi.MviView
import io.shared.presentation.podcast.details.PodcastDetailsView.*

interface PodcastDetailsView : MviView<Intent, Model, Effect> {

    sealed class Intent {

        data class TrackClick(val trackItem: TrackItem) : Intent()
        data class PlayPauseClick(val trackItem: TrackItem) : Intent()

    }

    data class Model(
        val logo: String = "",
        val title: String = "",
        val tracksWithState: List<TrackPlaybackStateItem> = emptyList(),
        val playlist: Playlist? = null,
        val headerColor: Int = 0
    ) : Persistable

    sealed class Effect : Persistable {

        object NavigateToPlayer : Effect()

        data class PodcastError(val message: ResourceString) : Effect()
        data class PlayerError(val message: ResourceString) : Effect()

    }

}