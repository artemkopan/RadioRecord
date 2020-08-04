package io.radio.shared.store.podcasts.details

import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


val podcastDetailsModule = module {

    factory { (podcastId: Int) ->
        PodcastDetailsByIdBootstrapper(
            podcastId
        )
    }

    factory {
        PodcastDetailsLoadMiddleware(
            get(),
            get()
        )
    }

    factory { (podcastId: Int) ->
        PodcastDetailsStoreFactory(
            get(),
            get(parameters = { parametersOf(podcastId) })
        )
    }

}

