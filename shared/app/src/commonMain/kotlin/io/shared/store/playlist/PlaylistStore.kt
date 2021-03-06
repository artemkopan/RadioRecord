package io.shared.store.playlist

import io.shared.core.Loggable
import io.shared.core.Persistable
import io.shared.model.Playlist
import io.shared.model.TrackItem
import io.shared.model.TrackPlaybackStateItem
import io.shared.mvi.*
import io.shared.store.playlist.PlaylistStore.*
import kotlinx.coroutines.CoroutineScope

interface PlaylistStore : Store<Action, Result, State> {

    sealed class Action {
        data class ObserveTracksMediaState(val tracks: List<TrackItem>) : Action()
    }

    sealed class Result {

        data class TracksWithMediaState(
            val playlist: Playlist,
            val tracks: List<TrackPlaybackStateItem>
        ) : Result(), Loggable {
            override fun toLogMessage(): String {
                return playlist.toLogMessage() + "\n${
                    tracks.joinToString {
                        """Track[id = ${it.track.id}, title = ${it.track.title}]; State[${it.state}]"; Error[${it.error}]"""
                    }
                }"
            }
        }

    }

    data class State(
        val playlist: Playlist? = null,
        val tracks: List<TrackPlaybackStateItem> = emptyList()
    ) : Persistable, Loggable {
        override fun toLogMessage(): String {
            return playlist?.toLogMessage().orEmpty()
        }
    }
}

class PlaylistStoreFactory(
    private val playlistObserveTracksStateMiddleware: PlaylistObserveTracksStateMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        tag: String,
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): PlaylistStore {
        return object : StoreImpl<Action, Result, State>(
            tag,
            coroutineScope,
            listOf(playlistObserveTracksStateMiddleware),
            emptyList(),
            ReducerImpl,
            stateStorage.getOrDefault(tag) { State() }
        ), PlaylistStore {}
    }

    private object ReducerImpl :
        Reducer<Result, State> {
        override fun reduce(result: Result, state: State): State = with(result) {
            when (this) {
                is Result.TracksWithMediaState -> state.copy(playlist = playlist, tracks = tracks)
            }
        }
    }
}