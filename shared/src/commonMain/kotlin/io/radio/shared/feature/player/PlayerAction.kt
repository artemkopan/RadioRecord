package io.radio.shared.feature.player


sealed class PlayerAction {

    object PlayPauseClicked : PlayerAction()

    data class PositionChanged(val position: Int, val isScrubbing: Boolean) : PlayerAction()

    object NextClicked : PlayerAction()

    object PreviousClicked : PlayerAction()

    object ForwardClicked : PlayerAction()

    object RewindClicked : PlayerAction()

    data class Buffering(val isBuffering: Boolean) : PlayerAction()
}