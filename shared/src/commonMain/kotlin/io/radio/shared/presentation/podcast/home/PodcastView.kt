package io.shared.presentation.podcast.home

import io.shared.core.Persistable
import io.shared.mvi.MviView
import io.radio.shared.model.Podcast
import io.radio.shared.model.ResourceString
import io.shared.presentation.podcast.details.PodcastDetailsParams
import io.shared.presentation.podcast.home.PodcastView.*

interface PodcastView : MviView<Intent, Model, Effect> {

    sealed class Intent {

        data class SelectPodcast(val podcast: Podcast) : Intent()

    }

    data class Model(val isLoading: Boolean, val data: List<Podcast>) : Persistable

    sealed class Effect : Persistable {

        data class Error(val message: ResourceString) : Effect()

        data class NavigateToDetails(val params: PodcastDetailsParams) : Effect()
    }

}