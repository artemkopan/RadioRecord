package io.radio.shared.store.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlaybackState
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PlayerPrepareMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transform {
            if (it !is Action.Prepare) {
                return@transform
            }

            val isCurrentTrack = mediaPlayer.trackFlow.value.data == it.track

            if (isCurrentTrack && !it.forcePrepareIfTrackPrepared && !it.switchPlaybackIfTrackPrepared) {
                return@transform
            }

            val mediaState = mediaPlayer.playbackStateFlow.value

            if (!isCurrentTrack || (isCurrentTrack && it.forcePrepareIfTrackPrepared)) {
                mediaPlayer.prepare(it.track, createPlaylist(it.track, it.tracks), it.autoPlay)
            }

            if (isCurrentTrack && it.switchPlaybackIfTrackPrepared) {
                if (mediaState is PlaybackState.Play) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.play()
                }
            }
        }
    }

    private fun createPlaylist(track: TrackItem, tracks: List<TrackItem>): Playlist? {
        return tracks.indexOfFirst { it.id == track.id }.takeIf { it >= 0 }?.let {
            Playlist(tracks, it)
        }
    }

}