package io.shared.store.podcasts.home

import io.shared.mvi.*
import io.shared.store.podcasts.home.PodcastStore.*
import kotlinx.coroutines.CoroutineScope

class PodcastStoreFactory(
    private val loadPodcastMiddleware: LoadPodcastMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        tag: String,
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): PodcastStore {
        return object : StoreImpl<Action, Result, State>(
            tag = tag,
            coroutineScope = coroutineScope,
            middlewareList = listOf(loadPodcastMiddleware),
            bootstrapperList = listOf(SimpleBootstrapper(Action.LoadPodcast)),
            reducer = ReducerImpl,
            initialState = stateStorage.getOrDefault("podcast-store_$tag") { State() }
        ), PodcastStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {
        override fun reduce(result: Result, state: State): State = with(result) {
            when (this) {
                Result.PodcastListLoading -> state.copy(isLoading = true, error = null)
                is Result.PodcastListLoaded -> state.copy(
                    isLoading = false,
                    error = null,
                    podcastList = data
                )
                is Result.PodcastListError -> state.copy(isLoading = false, error = throwable)
            }
        }
    }
}