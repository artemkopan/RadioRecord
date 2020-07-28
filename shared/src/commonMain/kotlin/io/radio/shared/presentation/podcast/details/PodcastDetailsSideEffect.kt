package io.radio.shared.presentation.podcast.details

import io.radio.shared.model.PodcastDetails
import io.radio.shared.model.TrackItem

sealed class PodcastDetailsSideEffect {
    object GetPodcastDetails : PodcastDetailsSideEffect()
    data class GetPodcastTrackList(val podcastDetails: PodcastDetails) : PodcastDetailsSideEffect()
    data class ObserveTracksState(val track: List<TrackItem>) : PodcastDetailsSideEffect()
}