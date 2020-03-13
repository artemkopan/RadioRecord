package io.radio.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.radio.presentation.stations.StationViewModel
import io.radio.shared.presentation.viewmodel.ViewModelKey

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(StationViewModel::class)
    internal abstract fun bindStationViewModel(recommendedViewModel: StationViewModel): ViewModel

}