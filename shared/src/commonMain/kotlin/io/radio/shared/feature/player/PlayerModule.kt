package io.radio.shared.feature.player

import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.feature.player.middleware.*
import org.koin.dsl.module

val playerModule = module {

    factory { PlayerChangeTrackMiddleware(get()) }
    factory { PlayerObserveEventsMiddleware(get(), get()) }
    factory { PlayerPlayPauseMiddleware(get()) }
    factory { PlayerPlayPauseMiddleware2(get()) }
    factory { PlayerSeekMiddleware(get(), get()) }
    factory { PlayerSlipMiddleware(get(), get()) }
    factory { PlayerReducer() }

    factory { (state: StateStorage) -> PlayerViewModel(state, get()) }

}