package io.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.shared.mvi.Middleware
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlayerStore.*
import io.shared.store.player.PlayerStore.Result.PlaylistAvailability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

class PlayerObserveMetaDataMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return mediaPlayer.playerMetaDataFlow.map {
            PlaylistAvailability(
                availableSeeking = it.data?.availableSeeking ?: false,
                availablePrevious = it.data?.availablePrevious ?: false,
                availableRewind = it.data?.availableRewind ?: false,
                availableFastForward = it.data?.availableFastForward ?: false,
                availableNext = it.data?.availableNext ?: false
            ) as Result
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.PlaybackError(cause)); true }
    }

}