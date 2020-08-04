package io.radio.shared.store.podcasts.details

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Store
import io.radio.shared.model.PodcastDetails
import io.radio.shared.model.TrackItem
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*

interface PodcastDetailsStore : Store<Action, Result, State> {

    sealed class Action {

        data class LoadPodcastById(val id: Int) : Action()

    }

    sealed class Result {

        object Loading : Result()

        data class Error(val throwable: Throwable) : Result()

        data class Podcast(val podcastDetails: PodcastDetails, val tracks: List<TrackItem>) :
            Result()

    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val podcastDetails: PodcastDetails? = null,
        val tracks: List<TrackItem> = emptyList()
    ) : Persistable

}