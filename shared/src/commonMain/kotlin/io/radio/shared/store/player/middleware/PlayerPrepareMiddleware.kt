package io.radio.shared.store.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.MediaState
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform

class PlayerPrepareMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return actions.transform {
            val track: TrackItem
            val tracks: List<TrackItem>
            val autoPlay: Boolean
            val switchPlayback: Boolean

            when (it) {
                is Action.Prepare -> {
                    track = it.track
                    tracks = it.tracks
                    autoPlay = it.autoPlay
                    switchPlayback = true
                }
                is Action.PrepareOrSwitchPlayPause -> {
                    track = it.track
                    tracks = it.tracks
                    autoPlay = it.autoPlay
                    switchPlayback = false
                }
                else -> return@transform
            }

            val isCurrentTrack = mediaPlayer.trackFlow.value.data == track

            if (isCurrentTrack && !switchPlayback) {
                return@transform
            }

            val mediaState = mediaPlayer.stateFlow.value

            if (isCurrentTrack) {
                if (mediaState is MediaState.Play) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.play()
                }
            } else {
                mediaPlayer.prepare(track, createPlaylist(track, tracks), autoPlay)
            }
        }
    }

    private fun createPlaylist(track: TrackItem, tracks: List<TrackItem>): Playlist? {
        return tracks.indexOfFirst { it.id == track.id }.takeIf { it >= 0 }?.let {
            Playlist(tracks, it)
        }
    }

}