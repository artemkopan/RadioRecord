package io.radio.shared.store.podcasts.home

import io.radio.shared.base.mvi.Reducer
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.StoreImpl
import io.radio.shared.base.viewmodel.StateStorage
import kotlinx.coroutines.CoroutineScope

class PodcastStoreFactory(
    private val loadPodcastBootstrapper: LoadPodcastBootstrapper,
    private val loadPodcastMiddleware: LoadPodcastMiddleware
) : StoreFactory<PodcastStore.Action, PodcastStore.Result, PodcastStore.State> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): PodcastStore {
        return object : StoreImpl<PodcastStore.Action, PodcastStore.Result, PodcastStore.State>(
            coroutineScope,
            listOf(loadPodcastMiddleware),
            listOf(loadPodcastBootstrapper),
            ReducerImpl,
            PodcastStore.State()
        ), PodcastStore {}
    }


    private object ReducerImpl :
        Reducer<PodcastStore.Result, PodcastStore.State> {
        override fun reduce(result: PodcastStore.Result, state: PodcastStore.State): PodcastStore.State = with(result) {
            when (this) {
                PodcastStore.Result.Loading -> state.copy(isLoading = true, error = null)
                is PodcastStore.Result.Success -> state.copy(isLoading = false, error = null, data = data)
                is PodcastStore.Result.Error -> state.copy(isLoading = false, error = throwable)
            }
        }
    }
}