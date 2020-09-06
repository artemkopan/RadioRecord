package io.shared.store.podcasts.details

import io.shared.mvi.*
import io.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.CoroutineScope

class PodcastDetailsStoreFactory(
    private val podcastDetailsLoadMiddleware: PodcastDetailsLoadMiddleware,
    private val podcastDetailsByIdBootstrapper: PodcastDetailsByIdBootstrapper
) : StoreFactory<Action, Result, State> {

    override fun create(
        tag: String,
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): PodcastDetailsStore {
        return object : StoreImpl<Action, Result, State>(
            tag = tag,
            coroutineScope = coroutineScope,
            middlewareList = listOf(podcastDetailsLoadMiddleware),
            bootstrapperList = listOf(podcastDetailsByIdBootstrapper),
            reducer = ReducerImpl,
            initialState = stateStorage.getOrDefault("podcast-details-state") { State() }
        ), PodcastDetailsStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {

        override fun reduce(result: Result, state: State): State = with(result) {
            when (this) {
                Result.Loading -> state.copy(isLoading = true, error = null)
                is Result.Error -> state.copy(error = throwable)
                is Result.Podcast -> state.copy(
                    error = null,
                    podcastDetails = podcastDetails,
                    tracks = tracks
                )
            }
        }

    }
}