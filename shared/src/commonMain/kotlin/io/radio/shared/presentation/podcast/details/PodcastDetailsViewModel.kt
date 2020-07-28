package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewModel

class PodcastDetailsViewModel(
    private val stateStorage: StateStorage,
    private val podcastDetailsStoreFactory: PodcastDetailsStoreFactory
) : ViewModel() {

    val store by lazyNonSafety {
        podcastDetailsStoreFactory.create(scope, stateStorage)
    }

}