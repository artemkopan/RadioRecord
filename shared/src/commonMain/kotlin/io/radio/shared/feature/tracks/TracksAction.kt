package io.radio.shared.feature.tracks

import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem

sealed class TracksAction {

    data class Prepare(val track: TrackItem, val playlist: Playlist, val autoPlay: Boolean) :
        TracksAction()

}