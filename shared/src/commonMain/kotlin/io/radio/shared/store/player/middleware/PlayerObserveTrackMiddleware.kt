package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.*

class PlayerObserveTrackMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
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