package io.shared.presentation.player

import io.shared.mvi.Binder
import io.shared.mvi.bind
import io.shared.mvi.StateStorage
import io.shared.mvi.ViewBinder
import io.shared.mvi.ViewBinderHelper
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.formatters.TrackFormatter
import io.shared.presentation.player.PlayerView.*
import io.shared.store.player.PlayerStore
import io.shared.store.player.PlayerStore.Action
import io.shared.store.player.PlayerStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.DurationUnit
import kotlin.time.seconds

class PlayerViewBinder(
    stateStorage: StateStorage,
    playerStoreFactory: PlayerStoreFactory,
    private val trackFormatter: TrackFormatter,
    private val errorFormatter: ErrorFormatter
) : ViewBinder(), Binder<PlayerView> {

    private val store = playerStoreFactory.create(scope, stateStorage)
    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)

    init {
        store.stateFlow.onEach {
            it.dispatchModel()
            it.dispatchEvent()
        }.launchIn(scope)
    }


    override suspend fun bind(view: PlayerView) {
        bind {
            helper bindTo view
            view.intents.mapToStoreActions() bindTo store
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

    private suspend fun PlayerStore.State.dispatchModel() {
        val currentDuration = scrubbingPosition ?: currentDuration
        val currentDurationSec = currentDuration?.toInt(DurationUnit.SECONDS) ?: 0
        val currentDurationFormatted = currentDuration?.let(trackFormatter::formatDuration)
            .orEmpty()

        Model(
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
                Model.Slip.Forward(trackFormatter.formatDuration(it))
            } ?: rewindDuration?.let {
                Model.Slip.Rewind(trackFormatter.formatDuration(it))
            }
        ).also { helper.dispatchModel(it) }
    }

    private suspend fun PlayerStore.State.dispatchEvent() {
        if (error != null) {
            helper.dispatchEffect(Effect.Error(errorFormatter.format(error)))
        }
    }

}