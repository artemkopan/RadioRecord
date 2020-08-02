package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import kotlinx.coroutines.flow.*

class PlayerObserveTimelineMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return mediaPlayer.trackTimeLineFlow.map {
            val data = it.data ?: return@map Result.TimeLine.None
            Result.TimeLine.Changed(
                data.currentPosition,
                data.totalDuration
            ) as Result
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }


}