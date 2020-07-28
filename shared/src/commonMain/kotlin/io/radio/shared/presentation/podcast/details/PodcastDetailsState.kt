package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.Persistable
import io.radio.shared.model.TrackItemWithMediaState

data class PodcastDetailsState(
    val loading: Boolean = false,
    val tracks: List<TrackItemWithMediaState> = emptyList(),
    val logo: String = "",
    val title: String = ""
) : Persistable