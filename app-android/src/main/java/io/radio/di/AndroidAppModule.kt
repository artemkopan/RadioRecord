package io.radio.di

import io.radio.data.AppResourcesImpl
import io.radio.di.Qualifier.PlayerCoroutineQualifier
import io.radio.presentation.createPlayerPendingIntent
import io.shared.resources.AppResources
import io.shared.store.player.PlayerNotificationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.Executors

val androidAppModule = module {

    single<AppResources> { AppResourcesImpl(get()) }

    single(named(PlayerCoroutineQualifier)) {
        CoroutineScope(
            SupervisorJob() + Executors.newFixedThreadPool(1).asCoroutineDispatcher()
        )
    }

    single {
        PlayerNotificationController(
            get(),
            get(named(PlayerCoroutineQualifier)),
            androidContext().createPlayerPendingIntent()
        )
    }

}
