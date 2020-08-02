package io.radio.shared.store.podcasts.home

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Store
import io.radio.shared.model.Podcast
import io.radio.shared.store.podcasts.home.PodcastStore.*

interface PodcastStore : Store<Action, Result, State> {


    sealed class Action {

        object LoadPodcast : Action()

    }

    sealed class Result {

        object Loading : Result()
        data class Success(val data: List<Podcast>) : Result()
        data class Error(val throwable: Throwable) : Result()

    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val data: List<Podcast> = emptyList()
    ) : Persistable

}


