package io.radio.shared.presentation.podcast.details

import io.radio.shared.model.TrackItemWithMediaState

sealed class PodcastDetailsIntent {

    data class Selection(val track: TrackItemWithMediaState) : PodcastDetailsIntent()
    data class PlayPause(val track: TrackItemWithMediaState) : PodcastDetailsIntent()

}