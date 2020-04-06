package io.radio.shared.domain.player

import io.radio.shared.base.Logger
import io.radio.shared.base.Optional
import io.radio.shared.base.toOptional
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

open class BasePlayerController(
    private val playerScope: CoroutineScope,
    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
    private val trackFormatter: TrackFormatter
) : PlayerController,
    PlayerStatesMediator {

    private val actionMutex = Mutex()
    private val trackInfoChannel =
        ConflatedBroadcastChannel<Optional<TrackMediaInfo>>(
            Optional.empty()
        )
    private val trackTimeLineChannel =
        ConflatedBroadcastChannel<Optional<TrackMediaTimeLine>>(
            Optional.empty()
        )
    private val playerMetaDataChannel by lazy {
        ConflatedBroadcastChannel<Optional<PlayerMetaData>>(
            Optional.empty()
        )
    }
    private val streamMetaDataChannel by lazy {
        ConflatedBroadcastChannel<Optional<StreamMetaData>>(
            Optional.empty()
        )
    }

    private val playlistChannel by lazy {
        ConflatedBroadcastChannel<Optional<Playlist>>(Optional.empty())
    }

    private val playerActionsChannel = BroadcastChannel<PlayerAction>(1)

    override val playerActionsFlow: Flow<PlayerAction>
        get() = playerActionsChannel.asFlow().buffer()

    override fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>> {
        return trackInfoChannel.asFlow()
    }

    override fun observeTrackTimeLine(): Flow<Optional<TrackMediaTimeLine>> {
        return trackTimeLineChannel.asFlow()
    }

    override fun observePlayerMetaData(): Flow<Optional<PlayerMetaData>> {
        return playerMetaDataChannel.asFlow()
    }

    override fun observeStreamMetaData(): Flow<Optional<StreamMetaData>> {
        return streamMetaDataChannel.asFlow()
    }

    override fun observePlaylist(): Flow<Optional<Playlist>> {
        return playlistChannel.asFlow()
    }

    override fun prepare(trackItem: TrackItem, playlist: Playlist, autoPlay: Boolean) {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Preparing(trackItem, playlist))
                playlistChannel.send(playlist.toOptional())
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
                playerMetaDataChannel.send(Optional.empty())
                streamMetaDataChannel.send(Optional.empty())
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

    override fun next() {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Next)
            }
        }
    }

    override fun previous() {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.Previous)
            }
        }
    }

    override fun setPosition(position: Duration) {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.SetPosition(position))
            }
        }
    }

    override fun seekTo(offset: Duration) {
        playerScope.launch {
            actionMutex.withLock {
                playerActionsChannel.send(PlayerAction.SeekTo(offset))
            }
        }
    }

    override fun postState(state: PlaybackState) {
        playerScope.launch {
            trackInfoChannel.send(copyTrack { copy(state = convertState(state)) })
        }
    }

    override fun postSideEffects(effect: PlayerSideEffect) {
        playerScope.launch {
            when (effect) {
                is PlayerSideEffect.MetaData -> {
                    playerMetaDataChannel.send(effect.value.toOptional())
                }
                is PlayerSideEffect.StreamMetaData -> {
                    streamMetaDataChannel.send(
                        StreamMetaData(
                            effect.title
                        ).toOptional()
                    )
                }
                is PlayerSideEffect.TrackChanged -> {
                    val data = playlistChannel.value.data
                    if (effect.track == null) {
                        trackInfoChannel.send(Optional.empty())
                        playlistChannel.send(data?.copy(position = -1).toOptional())
                        return@launch
                    }

                    data?.let {
                        playlistChannel.send(
                            it.copy(position = it.tracks.indexOf(effect.track)).toOptional()
                        )
                    }

                    trackInfoChannel.send(
                        trackMediaInfoCreatorUseCase.execute(
                            TrackMediaInfoCreatorUseCase.Params(
                                effect.track,
                                convertState(effect.playbackState!!)
                            )
                        ).toOptional()
                    )
                }
                is PlayerSideEffect.TrackPositionReset -> {
                    trackTimeLineChannel.send(Optional.empty())
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
            .data
            ?.block()
            .toOptional()
    }

    private fun convertState(state: PlaybackState): TrackMediaState {
        return when (state) {
            PlaybackState.PlayTrack -> {
                TrackMediaState.Play
            }
            PlaybackState.PauseTrack -> {
                TrackMediaState.Pause
            }
            PlaybackState.BufferingTrack -> {
                TrackMediaState.Buffering
            }
            PlaybackState.EndedTrack -> {
                TrackMediaState.Ended
            }
            is PlaybackState.Error -> {
                TrackMediaState.Error(state.throwable)
            }
        }
    }

}