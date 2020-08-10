package io.radio.di

import io.shared.store.image.imageModule
import io.shared.store.player.playerModule
import io.shared.store.playlist.playlistModule
import io.shared.store.podcasts.details.podcastDetailsModule
import io.shared.store.podcasts.home.podcastModule
import io.shared.store.stations.stationModule

val appModules
    get() = arrayOf(
        dataModule,
        networkModule,
        mappersModule,
        repositoryModule,
        domainModule,
        presentationModule,

        playerModule,
        imageModule,
        stationModule,
        podcastModule,
        podcastDetailsModule,
        playlistModule
    )