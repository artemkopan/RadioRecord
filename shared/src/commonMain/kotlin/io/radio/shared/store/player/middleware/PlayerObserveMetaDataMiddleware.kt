package io.radio.shared.store.player.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.PlayerStore.*
import io.radio.shared.store.player.PlayerStore.Result.PlaylistAvailability
import kotlinx.coroutines.flow.*

class PlayerObserveMetaDataMiddleware(
    private val mediaPlayer: MediaPlayer
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
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