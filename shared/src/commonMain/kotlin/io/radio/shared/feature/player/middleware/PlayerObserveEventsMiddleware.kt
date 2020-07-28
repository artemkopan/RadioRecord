package io.radio.shared.feature.player.middleware

import io.radio.shared.base.mvi.Middleware
import io.radio.shared.feature.player.MediaPlayer
import io.radio.shared.feature.player.MediaState
import io.radio.shared.feature.player.PlayerAction
import io.radio.shared.feature.player.PlayerState
import io.radio.shared.formatters.TrackFormatter
import kotlinx.coroutines.flow.*

class PlayerObserveEventsMiddleware(
    private val mediaPlayer: MediaPlayer,
    private val trackFormatter: TrackFormatter
) : Middleware<PlayerAction, PlayerState> {

    override fun dispatch(
        actions: Flow<PlayerAction>,
        states: StateFlow<PlayerState>
    ): Flow<PlayerAction> {
        return merge(
            trackChangedFlow(),
            mediaPlayer.stateFlow.map {
                when (it) {
                    MediaState.Idle -> PlayerAction.PlaybackIdle
                    MediaState.Buffering -> PlayerAction.PlaybackBuffering
                    MediaState.Play -> PlayerAction.PlaybackPlay
                    MediaState.Pause -> PlayerAction.PlaybackPause
                    MediaState.Ended -> PlayerAction.PlaybackEnded
                    is MediaState.Error -> PlayerAction.PlaybackError(it.throwable)
                }
            },
            mediaPlayer.trackTimeLineFlow.map {
                val data = it.data ?: return@map PlayerAction.TimeLine.None
                PlayerAction.TimeLine.Changed(
                    data.currentPosition,
                    trackFormatter.formatDuration(data.currentPosition),
                    data.totalDuration,
                    trackFormatter.formatDuration(data.totalDuration)
                )
            },
            mediaPlayer.playerMetaDataFlow.map {
                PlayerAction.PlaylistAvailability(
                    availableSeeking = it.data?.availableSeeking ?: false,
                    availablePrevious = it.data?.availablePrevious ?: false,
                    availableRewind = it.data?.availableRewind ?: false,
                    availableFastForward = it.data?.availableFastForward ?: false,
                    availableNext = it.data?.availableNext ?: false
                )
            }
        )
    }

    private fun trackChangedFlow(): Flow<PlayerAction.TrackChanged> {
        return mediaPlayer.trackFlow.transformLatest { trackOpt ->
            val track = trackOpt.data ?: run {
                emit(
                    PlayerAction.TrackChanged(
                        logo = "",
                        title = "",
                        subTitle = ""
                    )
                )
                return@transformLatest
            }

            fun createActionFromTrack(
                title: String = track.title
            ) = PlayerAction.TrackChanged(
                track.cover.data?.img ?: "",
                title,
                track.subTitle
            )


            if (track.source.isStream) {
                emitAll(mediaPlayer.streamMetaDataFlow.map { metaDataOpt ->
                    //prefer title from stream than track title. use in radio
                    val title = metaDataOpt.data?.title ?: track.title
                    return@map createActionFromTrack(title)
                })
            } else {
                emit(createActionFromTrack())
            }
        }
    }

}