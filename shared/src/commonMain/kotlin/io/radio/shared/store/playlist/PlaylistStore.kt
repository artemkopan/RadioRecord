package io.shared.store.playlist

import io.shared.core.Persistable
import io.shared.mvi.Reducer
import io.shared.mvi.Store
import io.shared.mvi.StoreFactory
import io.shared.mvi.StoreImpl
import io.shared.mvi.StateStorage
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackPlaybackStateItem
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
        ) : Result()

    }

    data class State(
        val playlist: Playlist? = null,
        val tracks: List<TrackPlaybackStateItem> = emptyList()
    ) : Persistable
}

class PlaylistStoreFactory(
    private val playlistObserveTracksStateMiddleware: PlaylistObserveTracksStateMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(coroutineScope: CoroutineScope, stateStorage: StateStorage): PlaylistStore {
        return object : StoreImpl<Action, Result, State>(
            coroutineScope,
            listOf(playlistObserveTracksStateMiddleware),
            emptyList(),
            ReducerImpl,
            State()
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