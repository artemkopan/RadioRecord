package io.shared.model

import io.shared.core.Loggable

data class Playlist(val tracks: List<TrackItem>, val position: Int) : Loggable {
    override fun toLogMessage(): String {
        return "Playlist[position = $position, tracks count = ${tracks.size}"
    }
}