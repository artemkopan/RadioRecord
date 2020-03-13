package io.radio.presentation.podcast.details

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.presentation.viewmodel.factory.AssistedSavedStateViewModelFactory

class PodcastDetailsViewModel @AssistedInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PodcastDetailsViewModel> {

        override fun create(savedStateHandle: SavedStateHandle?): PodcastDetailsViewModel

    }

}