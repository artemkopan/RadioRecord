package io.radio.di

import io.radio.shared.feature.player.playerModule

val appModules
    get() = arrayOf(
        dataModule,
        networkModule,
        mappersModule,
        repositoryModule,
        domainModule,
        presentationModule,
        playerModule
    )