package io.radio.shared.store.podcasts.details

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.Store
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.model.PodcastDetails
import io.radio.shared.model.TrackItem
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.CoroutineScope

interface PodcastDetailsStore :
    Store<Action, Result, State> {

    sealed class Action {

        data class LoadPodcastById(val id: Int) : Action()

    }

    sealed class Result {

        object Loading : Result()

        data class Error(val throwable: Throwable) : Result()

        data class Podcast(val podcastDetails: PodcastDetails, val tracks: List<TrackItem>) :
            Result()

    }

    data class State(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val podcastDetails: PodcastDetails? = null,
        val tracks: List<TrackItem> = emptyList()
    ) : Persistable

}


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