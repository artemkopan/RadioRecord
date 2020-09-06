package io.shared.store.podcasts.details

import io.shared.mvi.Bootstrapper
import io.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PodcastDetailsByIdBootstrapper(
    private val podcastId: Int
) : Bootstrapper<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: (State) -> Unit
    ): Flow<Action> {
        return flow { emit(Action.LoadPodcastById(podcastId)) }
    }
}