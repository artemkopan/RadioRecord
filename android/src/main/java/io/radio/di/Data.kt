package io.radio.di

import io.radio.data.AppResourcesImpl
import io.radio.data.player.AndroidPlayerController
import io.radio.di.Qualifier.PlayerCoroutine
import io.radio.presentation.createPlayerPendingIntent
import io.radio.shared.domain.configs.SystemConfig
import io.radio.shared.domain.configs.SystemConfigImpl
import io.radio.shared.domain.player.BasePlayerController
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.resources.AppResources
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.notifications.PlayerNotificationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.Executors

val dataModule = module {

    single<SystemConfig> { SystemConfigImpl() }
    single<AppResources> { AppResourcesImpl(get()) }

    single(named(PlayerCoroutine)) {
        CoroutineScope(
            SupervisorJob() + Executors.newFixedThreadPool(1).asCoroutineDispatcher()
        )
    }

    single {
        PlayerNotificationController(
            get(),
            get(named(PlayerCoroutine)),
            androidContext().createPlayerPendingIntent()
        )
    }

    single { BasePlayerController(get(named(PlayerCoroutine)), get(), get()) }

    single<PlayerController> {
        AndroidPlayerController(
            get(),
            get(named(PlayerCoroutine)),
            get(),
            get()
        )
    }

    single {
        MediaPlayer(get(), get())
    }

}
