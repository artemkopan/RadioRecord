package io.radio.shared.store.playlist

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackMediaStateItem
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.MediaState
import io.radio.shared.store.playlist.PlaylistStore.*
import kotlinx.coroutines.flow.*

class PlaylistObserveTracksStateMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return actions.transformLatest { action ->
            if (action !is Action.ObserveTracksMediaState || action.tracks.isEmpty()) {
                return@transformLatest
            }

            emitAll(combine(mediaPlayer.trackFlow, mediaPlayer.stateFlow) { currentTrack, state ->
                var position: Int = -1
                val tracksWithState = action.tracks.mapIndexed { index, track ->
                    TrackMediaStateItem(
                        track,
                        if (track.id == currentTrack.data?.id) {
                            position = index
                            state
                        } else {
                            MediaState.Idle
                        }
                    )
                }
                Result.TracksWithMediaState(
                    Playlist(action.tracks, position),
                    tracksWithState
                )
            })
        }.flowOn(IoDispatcher)
    }

}
