package io.shared.store.podcasts.home

import io.shared.core.Persistable
import io.shared.mvi.Store
import io.radio.shared.model.Podcast
import io.shared.store.podcasts.home.PodcastStore.*

interface PodcastStore : Store<Action, Result, State> {


    sealed class Action {

        object LoadPodcast : Action()

    }

    sealed class Result {

        object PodcastListLoading : Result()
        data class PodcastListLoaded(val data: List<Podcast>) : Result()
        data class PodcastListError(val throwable: Throwable) : Result()
    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val podcastList: List<Podcast> = emptyList()
    ) : Persistable

}


