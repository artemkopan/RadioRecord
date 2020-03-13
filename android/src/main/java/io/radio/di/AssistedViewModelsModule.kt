package io.radio.di

import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.radio.presentation.podcast.details.PodcastDetailsViewModel
import io.radio.shared.presentation.viewmodel.ViewModelKey
import io.radio.shared.presentation.viewmodel.factory.AssistedSavedStateViewModelFactory

@AssistedModule
@Module(includes = [AssistedInject_AssistedViewModelsModule::class])
abstract class AssistedViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PodcastDetailsViewModel::class)
    abstract fun providePodcastDetailsViewModelFactory(factory: PodcastDetailsViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

}