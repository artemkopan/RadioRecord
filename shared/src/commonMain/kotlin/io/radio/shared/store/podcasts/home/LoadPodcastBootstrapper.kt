package io.radio.shared.store.podcasts.home

import io.radio.shared.base.mvi.Bootstrapper
import io.radio.shared.store.podcasts.home.PodcastStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class LoadPodcastBootstrapper : Bootstrapper<Action, Result, State> {
    override fun accept(
        actions: Flow<Action>,
        results: Flow<Result>,
        stateFlow: StateFlow<State>
    ): Flow<Action> {
        return flowOf(Action.LoadPodcast)
    }
}