package io.shared.store.podcasts.home

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider


val podcastModule = DI.Module("podcast") {

    bind() from provider{ PodcastStoreFactory(instance()) }
    bind() from provider{ LoadPodcastMiddleware(instance()) }

}