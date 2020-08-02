package io.radio.di

import io.radio.shared.store.image.imageModule
import io.radio.shared.store.player.playerModule
import io.radio.shared.store.playlist.playlistModule
import io.radio.shared.store.podcasts.details.podcastDetailsModule
import io.radio.shared.store.podcasts.home.podcastModule
import io.radio.shared.store.stations.stationModule

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