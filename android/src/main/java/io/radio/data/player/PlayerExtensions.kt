package io.radio.data.player

import com.google.android.exoplayer2.Player
import io.radio.shared.model.TrackItem

fun Player.currentTrack(): TrackItem? {
    return currentTag as? TrackItem
}