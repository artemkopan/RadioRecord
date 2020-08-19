package io.shared.presentation

import io.shared.mvi.StateStorage
import io.shared.presentation.player.PlayerViewBinder
import io.shared.presentation.podcast.details.PodcastDetailsParams
import io.shared.presentation.podcast.details.PodcastDetailsViewBinder
import io.shared.presentation.podcast.home.PodcastViewBinder
import io.shared.presentation.stations.StationViewBinder
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val presentationModule = module {

    factory { (state: StateStorage) -> StationViewBinder(state, get(), get()) }
    factory { (state: StateStorage) -> PodcastViewBinder(state, get(), get(), get(), get()) }

    factory { (state: StateStorage) ->
        val params = state.get<PodcastDetailsParams>("params")

        PodcastDetailsViewBinder(
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