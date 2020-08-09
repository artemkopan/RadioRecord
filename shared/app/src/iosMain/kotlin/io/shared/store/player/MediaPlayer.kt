package io.shared.store.player

import io.shared.core.Optional
import io.shared.model.Playlist
import io.shared.model.TrackItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

actual class MediaPlayer {

    private val playbackStateMutableFlow = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    actual val playbackStateFlow: StateFlow<PlaybackState> = playbackStateMutableFlow

    private val trackMutableFlow = MutableStateFlow<Optional<TrackItem>>(Optional.empty())
    actual val trackFlow: StateFlow<Optional<TrackItem>> = trackMutableFlow

    private val trackTimeLineMutableFlow = MutableStateFlow<Optional<TimeLine>>(Optional.empty())
    actual val trackTimeLineFlow: StateFlow<Optional<TimeLine>> = trackTimeLineMutableFlow

    private val playerMetaDataMutableFlow =
        MutableStateFlow<Optional<PlayerMetaData>>(Optional.empty())
    actual val playerMetaDataFlow: StateFlow<Optional<PlayerMetaData>> = playerMetaDataMutableFlow

    private val streamMetaDataMutableFlow =
        MutableStateFlow<Optional<StreamMetaData>>(Optional.empty())
    actual val streamMetaDataFlow: StateFlow<Optional<StreamMetaData>> = streamMetaDataMutableFlow

    private val playlistMutableFlow = MutableStateFlow<Optional<Playlist>>(Optional.empty())
    actual val playlistFlow: StateFlow<Optional<Playlist>> = playlistMutableFlow


    actual suspend fun prepare(
        trackItem: TrackItem,
        playlist: Playlist?,
        autoPlay: Boolean
    ) {
    }

    actual suspend fun release() {
    }

    actual suspend fun play() {
    }

    actual suspend fun pause() {
    }

    actual suspend fun slip(offset: Duration) {
    }

    actual suspend fun seekTo(position: Duration) {
    }

    actual suspend fun next() {
    }

    actual suspend fun previous() {
    }

}