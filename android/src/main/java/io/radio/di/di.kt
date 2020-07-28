package io.radio.di

import io.radio.shared.feature.player.playerModule
import io.radio.shared.presentation.podcast.podcastModule

val appModules
    get() = arrayOf(
        dataModule,
        networkModule,
        mappersModule,
        repositoryModule,
        domainModule,
        presentationModule,
        playerModule,
        podcastModule
    )