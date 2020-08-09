package io.radio.shared.model

import io.shared.model.TrackItem
import io.shared.store.player.PlaybackState

data class TrackPlaybackStateItem(
    val track: TrackItem,
    val state: PlaybackState,
    val durationFormatted: String
)
