package io.radio.shared.store.player

import io.radio.shared.store.player.middleware.*
import org.koin.dsl.module

val playerModule = module {

    factory { PlayerChangeTrackMiddleware(get()) }
    factory { PlayerObserveMetaDataMiddleware(get()) }
    factory { PlayerObserveStateMiddleware(get()) }
    factory { PlayerObserveTimelineMiddleware(get()) }
    factory { PlayerObserveTrackMiddleware(get()) }
    factory { PlayerPlayPauseMiddleware(get()) }
    factory { PlayerSeekMiddleware(get()) }
    factory { PlayerSlipMiddleware(get(), get()) }
    factory { PlayerPrepareMiddleware(get()) }

    factory { PlayerStoreFactory(get(), get(),get(),get(), get(), get(), get(), get(), get()) }

}