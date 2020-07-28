package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.Persistable

sealed class PodcastDetailsSignal : Persistable {

    data class Error(val throwable: Throwable) : PodcastDetailsSignal()

}