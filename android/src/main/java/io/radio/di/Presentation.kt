package io.radio.di

import io.radio.shared.base.viewmodel.ViewModelParams
import io.radio.shared.presentation.player.PlayerViewModel
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModel
import io.radio.shared.presentation.stations.StationsViewModel
import org.koin.dsl.module

val presentationModule = module {

    factory { (params: ViewModelParams) ->
        PodcastDetailsViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            params
        )
    }

    factory { PodcastsViewModel(get(), get(), get()) }
    factory { StationsViewModel(get(), get(), get(), get()) }
    factory { PlayerViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }

}