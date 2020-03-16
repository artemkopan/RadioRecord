package io.radio.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.radio.presentation.home.HomeViewModel
import io.radio.presentation.podcast.PodcastsViewModel
import io.radio.presentation.station.StationsViewModel
import io.radio.shared.presentation.viewmodel.ViewModelKey

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(recommendedViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PodcastsViewModel::class)
    internal abstract fun bindPodcastsViewModel(viewModel: PodcastsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StationsViewModel::class)
    internal abstract fun bindStationsViewModel(viewModel: StationsViewModel): ViewModel

}