package io.radio.shared.store.podcasts.details

import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.CoroutineScope

class PodcastDetailsStoreFactory(
    private val podcastDetailsLoadMiddleware: PodcastDetailsLoadMiddleware,
    private val podcastDetailsByIdBootstrapper: PodcastDetailsByIdBootstrapper
) : StoreFactory<Action, Result, State> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): PodcastDetailsStore {
        return object :
            StoreImpl<Action, Result, State>(
                coroutineScope,
                listOf(podcastDetailsLoadMiddleware),
                listOf(podcastDetailsByIdBootstrapper),
                ReducerImpl,
                State()
            ),
            PodcastDetailsStore {}
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