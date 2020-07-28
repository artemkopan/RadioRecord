package io.radio.shared.model

import io.radio.shared.feature.player.MediaState

data class TrackItemWithMediaState(
    val track: TrackItem,
    val state: MediaState,
    val durationFormatted: String
)
