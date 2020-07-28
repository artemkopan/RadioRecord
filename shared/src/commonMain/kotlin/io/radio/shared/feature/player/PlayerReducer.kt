package io.radio.shared.feature.player

import io.radio.shared.base.mvi.Reducer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.DurationUnit

class PlayerReducer : Reducer<PlayerState, PlayerAction, PlayerSideEffect> {

    override fun reduce(
        state: PlayerState,
        action: PlayerAction,
        sideEffects: MutableStateFlow<PlayerSideEffect?>
    ): PlayerState {
        return when (action) {
            PlayerAction.PlayPauseIntent,
            PlayerAction.PlayNextIntent,
            PlayerAction.PlayPreviousIntent,
            PlayerAction.SlipForwardIntent,
            PlayerAction.SlipRewindIntent,
            is PlayerAction.FindPositionIntent -> state

            is PlayerAction.PlaylistAvailability -> {
                state.copy(
                    isNextAvailable = action.availableNext,
                    isPreviousAvailable = action.availablePrevious,
                    isSeekAvailable = action.availableSeeking
                )
            }
            is PlayerAction.TrackChanged -> {
                state.copy(
                    logo = action.logo,
                    title = action.title,
                    subTitle = action.subTitle
                )
            }
            is PlayerAction.PlaybackError -> {
                sideEffects.value = PlayerSideEffect.Error(action.throwable)
                state
            }
            PlayerAction.PlaybackBuffering -> {
                state.copy(isLoading = true)
            }
            PlayerAction.PlaybackIdle -> {
                state.copy(isLoading = false, isPlaying = false)
            }
            PlayerAction.PlaybackEnded -> {
                state.copy(isLoading = false, isPlaying = false)
            }
            PlayerAction.PlaybackPlay -> {
                state.copy(isPlaying = true)
            }
            PlayerAction.PlaybackPause -> {
                state.copy(isPlaying = false)
            }
            is PlayerAction.PlaybackPostForward -> {
                sideEffects.value = PlayerSideEffect.Slip.Forward(action.stepDurationFormatted)
                state
            }
            is PlayerAction.PlaybackPostRewind -> {
                sideEffects.value = PlayerSideEffect.Slip.Rewind(action.stepDurationFormatted)
                state
            }
            PlayerAction.TimeLine.None -> {
                state.copy(
                    currentDuration = 0, currentDurationFormatted = "",
                    totalDuration = 0, totalDurationFormatted = ""
                )
            }
            is PlayerAction.TimeLine.Changed -> {
                state.copy(
                    currentDuration = action.currentPosition.toInt(DurationUnit.SECONDS),
                    currentDurationFormatted = action.currentPositionFormatted,
                    totalDuration = action.totalDuration.toInt(DurationUnit.SECONDS),
                    totalDurationFormatted = action.totalDurationFormatted
                )
            }
            is PlayerAction.TrackScrubbing -> {
                sideEffects.value = PlayerSideEffect.SeekInScrubbing(action.timeFormatted)
                state
            }
        }
    }

}