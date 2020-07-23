package io.radio.shared.feature.player

import io.radio.shared.feature.player.middleware.PlayerObserveEventsMiddleware
import org.koin.dsl.module

val playerModule = module {

    factory { PlayerObserveEventsMiddleware(get()) }

}