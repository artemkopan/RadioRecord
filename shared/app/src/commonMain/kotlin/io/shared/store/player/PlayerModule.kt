package io.shared.store.player

import io.shared.store.player.middleware.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val playerModule = DI.Module("player") {

    bind() from provider { PlayerChangeTrackMiddleware(instance()) }
    bind() from provider { PlayerObserveMetaDataMiddleware(instance()) }
    bind() from provider { PlayerObserveStateMiddleware(instance()) }
    bind() from provider { PlayerObserveErrorMiddleware(instance()) }
    bind() from provider { PlayerObserveTimelineMiddleware(instance()) }
    bind() from provider { PlayerObserveTrackMiddleware(instance()) }
    bind() from provider { PlayerPlayPauseMiddleware(instance()) }
    bind() from provider { PlayerSeekMiddleware(instance()) }
    bind() from provider { PlayerSlipMiddleware(instance(), instance()) }
    bind() from provider { PlayerPrepareMiddleware(instance()) }

    bind() from provider {
        PlayerStoreFactory(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance()
        )
    }

}