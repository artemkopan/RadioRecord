package io.shared.store.podcasts.details

import org.kodein.di.*


val podcastDetailsModule = DI.Module("podcastDetails") {

    bind() from factory { podcastId: Int ->
        PodcastDetailsByIdBootstrapper(
            podcastId
        )
    }

    bind<PodcastDetailsLoadMiddleware>() with provider {
        PodcastDetailsLoadMiddleware(
            instance(),
            instance()
        )
    }

    bind() from factory { podcastId: Int ->
        PodcastDetailsStoreFactory(
            instance(),
            instance(arg = podcastId)
        )
    }

}

