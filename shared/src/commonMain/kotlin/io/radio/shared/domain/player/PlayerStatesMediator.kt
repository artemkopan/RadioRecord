package io.radio.shared.domain.player

import io.radio.shared.model.Playlist
import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlayerStatesMediator {

    val playerActionsFlow: Flow<PlayerAction>

    fun postState(state: PlaybackState)
    fun postSideEffects(effect: PlayerSideEffect)
}

sealed class PlayerAction {

    data class Preparing(val track: TrackItem, val playlist: Playlist) : PlayerAction()
    object Release : PlayerAction()
    object Play : PlayerAction()
    object Pause : PlayerAction()
    object Next : PlayerAction()
    object Previous : PlayerAction()
    data class SetPosition(val position: Duration) : PlayerAction()
    data class SeekTo(val offset: Duration) : PlayerAction()

}

sealed class PlaybackState {

    object PlayTrack : PlaybackState()
    object PauseTrack : PlaybackState()
    object BufferingTrack : PlaybackState()
    object EndedTrack : PlaybackState()
    data class Error(val throwable: Throwable) : PlaybackState()

}

sealed class PlayerSideEffect {

    data class MetaData(val value: PlayerMetaData) : PlayerSideEffect()
    data class StreamMetaData(val title: String) : PlayerSideEffect()
    data class TrackChanged(val track: TrackItem?, val playbackState: PlaybackState?) :
        PlayerSideEffect()

    data class TrackPosition(
        val currentPosition: Duration,
        val bufferedPosition: Duration,
        val contentPosition: Duration
    ) : PlayerSideEffect()

    object TrackPositionReset : PlayerSideEffect()
}