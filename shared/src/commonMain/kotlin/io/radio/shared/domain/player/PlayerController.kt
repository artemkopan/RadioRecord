package io.radio.shared.domain.player

import io.radio.shared.base.Logger
import io.radio.shared.base.Optional
import io.radio.shared.base.getOrThrow
import io.radio.shared.base.toOptional
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaState
import io.radio.shared.model.TrackMediaTimeLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface PlayerController {

    fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>>

    fun observeTrackTimeLine(): Flow<Optional<TrackMediaTimeLine>>

    fun observePlayerMetaData(): Flow<Optional<PlayerMetaData>>

    fun prepare(trackItem: TrackItem, autoPlay: Boolean)

    fun release()

    fun destroy()

    fun setPosition(positionMs: Long)

    fun seekTo(offsetMs: Long)

    fun play()

    fun pause()

}

open class BasePlayerController(
    private val playerScope: CoroutineScope,
    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
    private val trackFormatter: TrackFormatter
) : PlayerController, PlayerStatesMediator {

    private val actionMutex = Mutex()
    private val trackInfoChannel = ConflatedBroadcastChannel<Optional<TrackMediaInfo>>(
        Optional.empty()
    )
    private val trackTimeLineChannel = ConflatedBroadcastChannel<Optional<TrackMediaTimeLine>>(
        Optional.empty()
    )
    private val metaDataChannel = ConflatedBroadcastChannel<Optional<PlayerMetaData>>(
        Optional.empty()
    )

    private val playerActionsChannel = BroadcastChannel<PlayerAction>(1)
    override val playerActionsFlow: Flow<PlayerAction> = playerActionsChannel.asFlow().buffer()

    override fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>> = trackInfoChannel.asFlow()
    override fun observeTrackTimeLine(): Flow<Optional<TrackMediaTimeLine>> =
        trackTimeLineChannel.asFlow()

    override fun observePlayerMetaData(): Flow<Optional<PlayerMetaData>> = metaDataChannel.asFlow()

    override fun prepare(trackItem: TrackItem, autoPlay: Boolean) {
        playerScope.launch {
            actionMutex.withLock {
                trackInfoChannel.send(
                    trackMediaInfoCreatorUseCase.execute(
                        TrackMediaInfoCreatorUseCase.Params(
                            trackItem,
                            TrackMediaState.Preparing
                        )
                    ).toOptional()
                )
                playerActionsChannel.send(PlayerAction.Preparing(trackItem.source))
                if (autoPlay) {
                    playerActionsChannel.send(PlayerAction.Play)
                }
            }
        }
    }

    override fun release() {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Release)
                trackInfoChannel.send(Optional.empty())
                trackTimeLineChannel.send(Optional.empty())
                metaDataChannel.send(Optional.empty())
            }
        }
    }

    override fun destroy() {
        Logger.w("Destroy method is not implemented")
    }

    override fun play() {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Play)
            }
        }
    }

    override fun pause() {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Pause)
            }
        }
    }

    override fun setPosition(positionMs: Long) {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.SetPosition(positionMs))
            }
        }
    }

    override fun seekTo(offsetMs: Long) {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.SeekTo(offsetMs))
            }
        }
    }

    override fun postState(state: PlayerState) {
        playerScope.launch {
            when (state) {
                PlayerState.PlayTrack -> {
                    trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Play) })
                }
                PlayerState.PauseTrack -> {
                    trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Pause) })
                }
                PlayerState.BufferingTrack -> {
                    trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Buffering) })
                }
                is PlayerState.Error -> {
                    trackInfoChannel.send(copyTrack { copy(state = TrackMediaState.Error(state.throwable)) })
                }
            }
        }
    }

    override fun postSideEffects(effect: PlayerSideEffect) {
        playerScope.launch {
            when (effect) {
                is PlayerSideEffect.MetaData -> {
                    metaDataChannel.send(effect.value.toOptional())
                }
                is PlayerSideEffect.TrackPosition -> {
                    trackTimeLineChannel.send(
                        TrackMediaTimeLine(
                            effect.currentPosition,
                            effect.bufferedPosition,
                            effect.contentPosition,
                            trackFormatter.formatDuration(effect.currentPosition),
                            trackFormatter.formatDuration(effect.contentPosition)
                        ).toOptional()
                    )
                }
            }
        }
    }

    private fun copyTrack(block: TrackMediaInfo.() -> TrackMediaInfo): Optional<TrackMediaInfo> {
        return trackInfoChannel.value
            .getOrThrow()
            .block()
            .toOptional()
    }

}


