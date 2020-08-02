package io.radio.shared.store.podcasts.home

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.podcasts.home.PodcastStore.*
import kotlinx.coroutines.flow.*

class LoadPodcastMiddleware(
    private val radioRepository: RadioRepository
) : Middleware<Action, Result, State> {
    override fun accept(actions: Flow<Action>, state: StateFlow<State>): Flow<Result> {
        return actions.transform {
            if (it is Action.LoadPodcast) {
                emit(Result.Loading)
                emit(Result.Success(radioRepository.getPodcasts()))
            }
        }.flowOn(IoDispatcher).retryWhen { cause, _ -> emit(Result.Error(cause)); true }
    }
}