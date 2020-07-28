package io.radio.shared.presentation.podcast

import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.feature.radio.podcasts.details.middleware.GetPodcastDetailsMiddleware
import io.radio.shared.feature.radio.podcasts.details.middleware.GetPodcastTrackListMiddleware
import io.radio.shared.feature.tracks.ObserveTracksStateMiddleware
import io.radio.shared.feature.tracks.PlayerTrackSelectionMiddleware
import io.radio.shared.presentation.podcast.details.PodcastDetailsStoreFactory
import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val podcastModule = module {

    factory { (podcastId: Int) -> GetPodcastDetailsMiddleware(get(), podcastId) }
    factory { GetPodcastTrackListMiddleware(get()) }
    factory { ObserveTracksStateMiddleware(get(), get()) }
    factory { PlayerTrackSelectionMiddleware(get()) }

    factory { (podcastId: Int) ->
        PodcastDetailsStoreFactory(
            get(parameters = { parametersOf(podcastId) }),
            get(),
            get()
        )
    }

    factory { (params: StateStorage, podcastId: Int) ->
        PodcastDetailsViewModel(
            params,
            get(parameters = { parametersOf(podcastId) })
        )
    }

}