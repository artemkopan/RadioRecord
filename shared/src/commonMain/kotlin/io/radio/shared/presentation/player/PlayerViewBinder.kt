package io.radio.shared.presentation.player

import io.radio.shared.base.extensions.formatTag
import io.radio.shared.base.mvi.bind
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewBinder
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.formatters.TrackFormatter
import io.radio.shared.presentation.player.PlayerView.Intent
import io.radio.shared.store.player.PlayerStore
import io.radio.shared.store.player.PlayerStore.Action
import io.radio.shared.store.player.PlayerStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.time.DurationUnit
import kotlin.time.seconds

class PlayerViewBinder(
    stateStorage: StateStorage,
    playerStoreFactory: PlayerStoreFactory,
    private val trackFormatter: TrackFormatter,
    private val errorFormatter: ErrorFormatter
) : ViewBinder() {

    private val store = playerStoreFactory.create(scope, stateStorage)

    suspend fun attachView(view: PlayerView) {
        bind {
            view.intents.mapToStoreActions() bindTo store
            store.stateFlow.mapToModel() bindTo view
            store.stateFlow.mapToEvent() bindTo view
        }
    }

    private fun Flow<Intent>.mapToStoreActions(): Flow<Action> {
        return map { intent ->
            when (intent) {
                Intent.PlayPause -> Action.SwitchPlayback
                Intent.PlayNext -> Action.PlayNext
                Intent.PlayPrevious -> Action.PlayPrevious
                Intent.SlipForward -> Action.SlipForward
                Intent.SlipRewind -> Action.SlipForward
                is Intent.FindPosition -> Action.FindPosition(
                    intent.position.seconds,
                    intent.isScrubbing
                )
            }
        }
    }

    private fun Flow<PlayerStore.State>.mapToModel(): Flow<PlayerView.Model> {
        return map { state ->
            with(state) {
                val currentDuration = scrubbingPosition ?: currentDuration
                val currentDurationSec = currentDuration?.toInt(DurationUnit.SECONDS) ?: 0
                val currentDurationFormatted = currentDuration?.let(trackFormatter::formatDuration)
                    .orEmpty()

                PlayerView.Model(
                    title = track?.title.orEmpty(),
                    subTitle = track?.subTitle.orEmpty(),
                    cover = track?.cover?.data?.img.orEmpty(),
                    isNextAvailable = isNextAvailable,
                    isPreviousAvailable = isPreviousAvailable,
                    isSeekingAvailable = isSeekAvailable,
                    isFastForwardAvailable = isFastForwardAvailable,
                    isRewindAvailable = isRewindAvailable,
                    currentDuration = currentDurationSec,
                    currentDurationFormatted = currentDurationFormatted,
                    totalDuration = totalDuration?.toInt(DurationUnit.SECONDS) ?: 0,
                    totalDurationFormatted = totalDuration?.let(trackFormatter::formatDuration)
                        .orEmpty(),
                    isLoading = isPreparing,
                    isPlaying = isPlaying,
                    slip = forwardDuration?.let {
                        PlayerView.Model.Slip.Forward(trackFormatter.formatDuration(it))
                    } ?: rewindDuration?.let {
                        PlayerView.Model.Slip.Rewind(trackFormatter.formatDuration(it))
                    }
                )
            }
        }
    }

    private fun Flow<PlayerStore.State>.mapToEvent(): Flow<PlayerView.Event> {
        return mapNotNull {
            with(it) {
                when {
                    error != null -> PlayerView.Event.Error(
                        errorFormatter.format(error),
                        error formatTag "track"
                    )
                    else -> null
                }
            }
        }
    }

}