package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.Store2Impl
import io.radio.shared.base.mvi.StoreFactory
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.feature.player.middleware.PlayerPlayPauseMiddleware2
import io.radio.shared.feature.radio.podcasts.details.middleware.*
import io.radio.shared.feature.tracks.*
import kotlinx.coroutines.CoroutineScope

class PodcastDetailsStoreFactory(
    private val getPodcastDetailsMiddleware: GetPodcastDetailsMiddleware,
    private val getPodcastTrackListMiddleware: GetPodcastTrackListMiddleware,
    private val observeTracksStateMiddleware: ObserveTracksStateMiddleware,
    private val playerTrackSelectionMiddleware: PlayerTrackSelectionMiddleware,
    private val playPauseMiddleware: PlayerPlayPauseMiddleware2
) : StoreFactory<PodcastDetailsIntent, PodcastDetailsSideEffect, PodcastDetailsState, PodcastDetailsSignal> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): Store2Impl<PodcastDetailsIntent, PodcastDetailsSideEffect, PodcastDetailsState, PodcastDetailsSignal> {
        return PodcastDetailsStore(
            coroutineScope,
            listOf(
                getPodcastDetailsMiddleware,
                getPodcastTrackListMiddleware,
                observeTracksStateMiddleware,
                playerTrackSelectionMiddleware
            )
        )
    }

    private inner class PodcastDetailsStore(
        coroutineScope: CoroutineScope,
        middlewareList: List<Middleware2<out Result>>
    ) : Store2Impl<PodcastDetailsIntent, PodcastDetailsSideEffect, PodcastDetailsState, PodcastDetailsSignal>(
        coroutineScope,
        middlewareList,
        PodcastDetailsState()
    ) {

        init {
            dispatchSideEffect(PodcastDetailsSideEffect.GetPodcastDetails)
        }

        override val intentMapper: PodcastDetailsIntent.() -> Action
            get() = {
                when (this) {
                    is PodcastDetailsIntent.Selection,
                    is PodcastDetailsIntent.PlayPause -> PlayerTrackSelectionAction()
                }
            }

        override val sideEffectMapper: PodcastDetailsSideEffect.() -> Action
            get() = {
                when (this) {
                    is PodcastDetailsSideEffect.GetPodcastTrackList -> GetPodcastTrackListAction(
                        podcastDetails
                    )
                    is PodcastDetailsSideEffect.ObserveTracksState -> ObserveTracksStateAction(track)
                    PodcastDetailsSideEffect.GetPodcastDetails -> GetPodcastDetailsAction
                }
            }

        override val reducer: Result.(PodcastDetailsState) -> PodcastDetailsState
            get() = { state ->
                when (this) {
                    is GetPodcastDetailsResult.Error -> {
                        dispatchSignal(PodcastDetailsSignal.Error(throwable))
                        state
                    }
                    is GetPodcastDetailsResult.Loading -> state.copy(loading = true)
                    is GetPodcastDetailsResult.Success -> {
                        state.copy(
                            loading = false,
                            logo = podcastDetails.cover,
                            title = podcastDetails.name
                        )
                    }
                    is GetPodcastTrackListResult -> {
                        dispatchSideEffect(PodcastDetailsSideEffect.ObserveTracksState(trackList))
                        state
                    }
                    is ObserveTracksStateResult.Error -> {
                        dispatchSignal(PodcastDetailsSignal.Error(throwable))
                        state
                    }
                    is ObserveTracksStateResult.Success -> {
                        state.copy(tracks = tracks)
                    }
                    is PlayerTrackSelectionResult.TrackAlreadyPrepared ->{
                        state
                    }
                    else -> throwNotImplemented(this)
                }
            }
    }
}