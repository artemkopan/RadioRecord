package io.shared.model

import io.shared.store.player.PlaybackError
import io.shared.store.player.PlaybackState

data class TrackPlaybackStateItem(
    val track: TrackItem,
    val state: PlaybackState,
    val error: PlaybackError?,
    val durationFormatted: String
)
