package io.radio.di

import io.radio.shared.base.viewmodel.ViewModelParams
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModel
import io.radio.shared.presentation.stations.StationsViewModel
import org.koin.dsl.module

val presentationModule = module {

    factory { (params: ViewModelParams) -> PodcastDetailsViewModel(params) }

    factory { PodcastsViewModel(get(), get(), get()) }
    factory { StationsViewModel(get()) }

}