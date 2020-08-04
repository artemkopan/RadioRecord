package io.radio.shared.store.podcasts.home

import org.koin.dsl.module

val podcastModule = module {

    factory { PodcastStoreFactory(get()) }
    factory { LoadPodcastMiddleware(get()) }

}