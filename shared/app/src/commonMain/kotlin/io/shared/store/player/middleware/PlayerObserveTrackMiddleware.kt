package io.shared.store.player.middleware

import io.shared.core.IoDispatcher
import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.*

class PlayerObserveTrackMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return mediaPlayer.trackFlow.transformLatest { trackOpt ->
            val track = trackOpt.data ?: run {
                emit(Result.TrackChanged(null))
                return@transformLatest
            }

            fun createActionFromTrack(title: String = track.title) =
                Result.TrackChanged(track.copy(title = title)) as Result

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
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }
}