package io.radio.shared.domain.player.playlist

import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.Flow

interface PlayerPlaylistManager {

    fun destroy()

    fun observePlaylistAvailability(): Flow<PlaylistAvailability>

    fun observePlaylist(): Flow<Playlist>

    fun setPlaylist(tracks: List<TrackItem>)

    fun clear()

    fun playNext()

    fun playPrevious()

    companion object {
        const val NO_POSITION = -1
    }

}

data class Playlist(val tracks: List<TrackItem>, val position: Int)

data class PlaylistAvailability(
    val nextAvailable: Boolean = false,
    val previousAvailable: Boolean = false
)