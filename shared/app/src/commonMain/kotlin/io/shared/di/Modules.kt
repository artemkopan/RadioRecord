package io.shared.di

import io.shared.mapper.mappersModule
import io.shared.network.networkModule
import io.shared.presentation.presentationModule
import io.shared.repo.repositoryModule
import io.shared.store.image.imageModule
import io.shared.store.player.playerModule
import io.shared.store.playlist.playlistModule
import io.shared.store.podcasts.details.podcastDetailsModule
import io.shared.store.podcasts.home.podcastModule
import io.shared.store.stations.stationModule

val commonModules = listOf(
    platformModule,
    networkModule,
    mappersModule,
    repositoryModule,
    presentationModule,

    playerModule,
    imageModule,
    stationModule,
    podcastModule,
    podcastDetailsModule,
    playlistModule
)
