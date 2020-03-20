package io.radio.presentation.podcast.details

import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.radio.shared.common.viewmodel.ViewModel
import io.radio.shared.common.viewmodel.factory.AssistedSavedStateViewModelFactory

class PodcastDetailsViewModel @AssistedInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle
) : ViewModel() {


    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<PodcastDetailsViewModel> {

        override fun create(savedStateHandle: SavedStateHandle?): PodcastDetailsViewModel

    }

}