package io.radio.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.radio.presentation.home.HomeViewModel
import io.radio.shared.presentation.viewmodel.ViewModelKey

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(recommendedViewModel: HomeViewModel): ViewModel

}