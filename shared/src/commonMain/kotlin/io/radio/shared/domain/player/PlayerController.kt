package io.radio.shared.domain.player

import io.radio.shared.base.MainDispatcher
import io.radio.shared.base.Optional
import io.radio.shared.base.getOrThrow
import io.radio.shared.base.toOptional
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaState
import io.radio.shared.model.TrackSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

interface PlayerController {

    fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>>

    fun prepare(trackItem: TrackItem)

    fun release()

    fun destroy()

    fun play()

    fun pause()

}

abstract class BasePlayerController(
    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase
) : PlayerController {

    protected open val scope = CoroutineScope(SupervisorJob() + MainDispatcher)
    protected val playerStates = BroadcastChannel<PlayerState>(1)

    private val trackInfoChannel = ConflatedBroadcastChannel<Optional<TrackMediaInfo>>(
        Optional.empty()
    )

    override fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>> = trackInfoChannel.asFlow()

    override fun destroy() {
        scope.cancel()
    }

    override fun prepare(trackItem: TrackItem) {
        scope.launch {
            trackInfoChannel.send(
                trackMediaInfoCreatorUseCase.execute(
                    TrackMediaInfoCreatorUseCase.Params(
                        trackItem,
                        TrackMediaState.Preparing
                    )
                ).toOptional()
            )
            playerStates.send(PlayerState.Preparing(trackItem.source))
            playerStates.send(PlayerState.Play)
        }
    }

    override fun release() {
        scope.launch {
            playerStates.send(PlayerState.Release)
            trackInfoChannel.send(Optional.empty())
        }
    }

    override fun play() {
        scope.launch {
            playerStates.send(PlayerState.Play)
        }
    }

    override fun pause() {
        scope.launch {
            playerStates.send(PlayerState.Pause)
        }
    }

    protected fun sendPlayTrack() {
        scope.launch { trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Play) }) }
    }

    protected fun sendPauseTrack() {
        scope.launch { trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Pause) }) }
    }

    protected fun sendBufferingTrack() {
        scope.launch { trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Buffering) }) }
    }

    protected fun sendError(throwable: Throwable) {
        scope.launch {
            trackInfoChannel.send(copyTrack {
                copy(state = TrackMediaState.Error(throwable))
            })
        }
    }

    private fun copyTrack(block: TrackMediaInfo.() -> TrackMediaInfo): Optional<TrackMediaInfo> {
        return trackInfoChannel.value
            .getOrThrow()
            .block()
            .toOptional()
    }

    protected sealed class PlayerState {

        class Preparing(val source: TrackSource) : PlayerState()
        object Release : PlayerState()
        object Play : PlayerState()
        object Pause : PlayerState()

    }
}
