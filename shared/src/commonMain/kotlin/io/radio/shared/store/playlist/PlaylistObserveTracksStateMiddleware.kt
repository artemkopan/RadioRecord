package io.radio.shared.store.playlist

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.formatters.TrackFormatter
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackPlaybackStateItem
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlaybackState
import io.radio.shared.store.playlist.PlaylistStore.*
import kotlinx.coroutines.flow.*

class PlaylistObserveTracksStateMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val trackFormatter: TrackFormatter
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transformLatest { action ->
            if (action !is Action.ObserveTracksMediaState || action.tracks.isEmpty()) {
                return@transformLatest
            }

            emitAll(combine(mediaPlayer.trackFlow, mediaPlayer.playbackStateFlow) { currentTrack, state ->
                var position: Int = -1
                val tracksWithState = action.tracks.mapIndexed { index, track ->
                    TrackPlaybackStateItem(
                        track,
                        if (track.id == currentTrack.data?.id) {
                            position = index
                            state
                        } else {
                            PlaybackState.Idle
                        },
                        trackFormatter.formatDuration(track.duration)
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
