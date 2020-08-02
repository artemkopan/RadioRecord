package io.radio.shared.model

import io.radio.shared.store.player.MediaState

data class TrackMediaStateItem(
    val track: TrackItem,
    val state: MediaState
)
