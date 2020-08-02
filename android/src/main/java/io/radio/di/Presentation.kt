package io.radio.di

import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.presentation.player.PlayerViewBinder
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.presentation.podcast.details.PodcastDetailsVewBinder
import io.radio.shared.presentation.podcast.home.PodcastViewBinder
import io.radio.shared.presentation.stations.StationViewBinder
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val presentationModule = module {

    factory { (state: StateStorage) -> StationViewBinder(state, get(), get()) }
    factory { (state: StateStorage) -> PodcastViewBinder(state, get(), get(), get(), get()) }

    factory { (state: StateStorage) ->
        val params = state.get<PodcastDetailsParams>("params")

        PodcastDetailsVewBinder(
            state,
            get(parameters = { parametersOf(params!!.id) }),
            get(),
            get(),
            get()
        )
    }

    factory { (state: StateStorage) ->
        PlayerViewBinder(
            state,
            get(),
            get(),
            get()
        )
    }

}