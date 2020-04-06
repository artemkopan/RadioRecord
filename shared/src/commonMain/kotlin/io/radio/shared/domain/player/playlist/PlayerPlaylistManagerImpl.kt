package io.radio.shared.domain.player.playlist

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Logger
import io.radio.shared.base.getOrThrow
import io.radio.shared.base.isNotEmpty
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.player.playlist.PlayerPlaylistManager.Companion.NO_POSITION
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlayerPlaylistManagerImpl(
    private val playerController: PlayerController
) : PlayerPlaylistManager {

    private val playlistChannel = ConflatedBroadcastChannel<Playlist>()
    private val playlistAvailabilityChannel = ConflatedBroadcastChannel(PlaylistAvailability())

    private val scope = CoroutineScope(SupervisorJob() + IoDispatcher)
    private val trackInfoChannel = ConflatedBroadcastChannel<TrackMediaInfo>()
    private val mutex = Mutex()
    private var playlist: List<TrackItem> = emptyList()

    init {
        playerController.observeTrackInfo()
            .onEach {
                if (it.isNotEmpty()) {
                    val track = it.getOrThrow()
                    trackInfoChannel.send(track)
                    when (track.state) {
                        TrackMediaState.Preparing -> {
                            val trackIndex = currentIndex()
                            updateAvailability(trackIndex)
                            playlistChannel.send(Playlist(playlist, trackIndex))
                        }
                        TrackMediaState.Ended -> playNext()
                    }
                }
            }
            .launchIn(scope)
    }

    override fun destroy() {
        scope.cancel()
    }

    override fun observePlaylistAvailability(): Flow<PlaylistAvailability> =
        playlistAvailabilityChannel.asFlow().distinctUntilChanged()

    override fun observePlaylist(): Flow<Playlist> = playlistChannel.asFlow()

    override fun setPlaylist(tracks: List<TrackItem>) {
        launchWithLock {
            playlist = tracks
            updateAvailability(currentIndex())
        }
    }

    override fun clear() {
        launchWithLock {
            playlist = emptyList()
            mutateAvailability { it.copy(nextAvailable = false, previousAvailable = false) }
        }
    }

    override fun playNext() {
        launchWithLock {
            val nextIndex = currentIndex() + 1
            Logger.d("Play next, index $nextIndex")
            if (playlist.isNotEmpty() && nextIndex < playlist.size) {
                playerController.prepare(playlist[nextIndex], true)
            }
        }
    }

    override fun playPrevious() {
        launchWithLock {
            val prevIndex = currentIndex() - 1
            Logger.d("Play previous, index $prevIndex")
            if (playlist.isNotEmpty() && prevIndex >= 0) {
                playerController.prepare(playlist[prevIndex], true)
            }
        }
    }


    private suspend fun mutateAvailability(block: (PlaylistAvailability) -> PlaylistAvailability) {
        playlistAvailabilityChannel.send(block(playlistAvailabilityChannel.value))
    }

    private suspend fun updateAvailability(trackIndex: Int) {
        val hasTrack = trackIndex >= 0
        val nextAvailable: Boolean = hasTrack && trackIndex + 1 < playlist.size
        val previousAvailable: Boolean = hasTrack && trackIndex - 1 >= 0
        mutateAvailability {
            it.copy(
                nextAvailable = nextAvailable,
                previousAvailable = previousAvailable
            )
        }
    }

    private suspend fun currentIndex(): Int {
        val currentTrack = playerController.observeTrackInfo().first().data
        return (currentTrack?.let { playlist.indexOf(it.track) } ?: NO_POSITION).also {
            Logger.d("Current index $it")
        }
    }

    private inline fun launchWithLock(crossinline block: suspend () -> Unit) {
        scope.launch {
            mutex.withLock {
                block()
            }
        }
    }

}