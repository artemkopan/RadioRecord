package io.radio.shared.feature.player

import io.radio.shared.base.mvi.Reducer

class PlayerReducer : Reducer<PlayerState, PlayerAction> {
    override fun reduce(state: PlayerState, action: PlayerAction): PlayerState {
        return when(action){
            PlayerAction.PlayPauseClicked -> TODO()
            PlayerAction.NextClicked -> TODO()
            PlayerAction.PreviousClicked -> TODO()
            PlayerAction.SlipForwardClicked -> TODO()
            PlayerAction.SlipRewindClicked -> TODO()
            is PlayerAction.Prepare -> TODO()
            PlayerAction.Release -> TODO()
            is PlayerAction.Seek -> TODO()
            is PlayerAction.PlaylistAvailability -> TODO()
            is PlayerAction.TrackChanged -> TODO()
            is PlayerAction.PlaybackError -> TODO()
            PlayerAction.PlaybackBuffering -> TODO()
            PlayerAction.PlaybackIdle -> TODO()
            PlayerAction.PlaybackEnded -> TODO()
            PlayerAction.PlaybackPlay -> TODO()
            PlayerAction.PlaybackPause -> TODO()
            is PlayerAction.PlaybackPostForward -> TODO()
            is PlayerAction.PlaybackPostRewind -> TODO()
            PlayerAction.TimeLine.None -> TODO()
            is PlayerAction.TimeLine.Changed -> TODO()
            is PlayerAction.TrackScrubbing -> TODO()
        }
    }
}