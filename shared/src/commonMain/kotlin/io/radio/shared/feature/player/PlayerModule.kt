package io.radio.shared.feature.player

import io.radio.shared.feature.player.middleware.ObserveMediaPlayerEventsMiddleware
import org.koin.dsl.module

val playerModule = module {

    factory { ObserveMediaPlayerEventsMiddleware(get()) }

}