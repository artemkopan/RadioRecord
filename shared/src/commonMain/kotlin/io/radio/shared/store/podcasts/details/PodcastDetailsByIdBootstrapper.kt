package io.radio.shared.store.podcasts.details

import io.radio.shared.base.mvi.Bootstrapper
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class PodcastDetailsByIdBootstrapper(
    private val podcastId: Int
) : Bootstrapper<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        results: Flow<Result>,
        stateFlow: StateFlow<State>
    ): Flow<Action> {
        return flow { emit(Action.LoadPodcastById(podcastId)) }
    }
}