package io.radio.shared.model

import io.radio.shared.store.player.PlaybackState

data class TrackPlaybackStateItem(
    val track: TrackItem,
    val state: PlaybackState,
    val durationFormatted: String
)
