package io.radio.di

import android.content.Context
import io.radio.data.AppResourcesImpl
import io.radio.di.Qualifier.PlayerCoroutineQualifier
import io.radio.presentation.createPlayerPendingIntent
import io.shared.resources.AppResources
import io.shared.store.player.PlayerNotificationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.util.concurrent.Executors

val androidAppModule = DI.Module("android-app") {

    bind<AppResources>() with singleton { AppResourcesImpl(instance()) }

    //share PlayerCoroutineQualifier between Service Holder and Notification Controller
    bind(tag = PlayerCoroutineQualifier) from singleton {
        CoroutineScope(
            SupervisorJob() + Executors.newFixedThreadPool(1).asCoroutineDispatcher()
        )
    }

    bind() from singleton {
        PlayerNotificationController(
            instance(),
            instance(tag = PlayerCoroutineQualifier),
            instance<Context>().createPlayerPendingIntent()
        )
    }

}
