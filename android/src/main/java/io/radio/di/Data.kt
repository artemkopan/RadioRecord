package io.radio.di

import io.radio.data.AppResourcesImpl
import io.radio.data.player.AndroidPlayerController
import io.radio.shared.domain.configs.SystemConfig
import io.radio.shared.domain.configs.SystemConfigImpl
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.player.playlist.PlayerPlaylistManager
import io.radio.shared.domain.player.playlist.PlayerPlaylistManagerImpl
import io.radio.shared.domain.resources.AppResources
import org.koin.dsl.module

val dataModule = module {

    single<SystemConfig> { SystemConfigImpl() }
    single<AppResources> { AppResourcesImpl(get()) }
    single<PlayerController> { AndroidPlayerController(get(), get(), get()) }
    single<PlayerPlaylistManager> { PlayerPlaylistManagerImpl(get()) }

}