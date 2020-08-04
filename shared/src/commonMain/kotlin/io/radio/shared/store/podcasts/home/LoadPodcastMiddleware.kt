package io.radio.shared.store.podcasts.home

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.podcasts.home.PodcastStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform

class LoadPodcastMiddleware(
    private val radioRepository: RadioRepository
) : Middleware<Action, Result, State> {
    override fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result> {
        return actionFlow.transform {
            if (it is Action.LoadPodcast) {
                emit(Result.PodcastListLoading)
                emit(Result.PodcastListLoaded(radioRepository.getPodcasts()))
            }
        }.flowOn(IoDispatcher).retryWhen { cause, _ -> emit(Result.PodcastListError(cause)); true }
    }
}