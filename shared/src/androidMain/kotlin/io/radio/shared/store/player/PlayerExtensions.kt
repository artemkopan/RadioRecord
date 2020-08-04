package io.radio.shared.store.player

import com.google.android.exoplayer2.Player
import io.radio.shared.model.TrackItem

inline val Player.currentTrack: TrackItem?
    get() {
        return currentTag as? TrackItem
    }