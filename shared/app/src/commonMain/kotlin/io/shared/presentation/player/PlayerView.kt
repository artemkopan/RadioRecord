package io.shared.presentation.player

import io.shared.core.Persistable
import io.shared.model.ResourceString
import io.shared.mvi.MviView
import io.shared.presentation.player.PlayerView.*


interface PlayerView : MviView<Intent, Model, Effect> {

    sealed class Intent {

        object PlayPause : Intent()
        object PlayNext : Intent()
        object PlayPrevious : Intent()
        object SlipForward : Intent()
        object SlipRewind : Intent()

        data class FindPosition(val position: Int, val isScrubbing: Boolean) : Intent()

    }

    data class Model(
        val title: String,
        val subTitle: String,
        val cover: String,
        val isNextAvailable: Boolean,
        val isPreviousAvailable: Boolean,
        val isSeekingAvailable: Boolean,
        val isFastForwardAvailable: Boolean,
        val isRewindAvailable: Boolean,
        val currentDuration: Int,
        val currentDurationFormatted: String,
        val totalDuration: Int,
        val totalDurationFormatted: String,
        val isLoading: Boolean,
        val isPlaying: Boolean,
        val slip: Slip?
    ) : Persistable {

        sealed class Slip {
            data class Rewind(val timeFormatted: String) : Slip()
            data class Forward(val timeFormatted: String) : Slip()
        }

    }

    sealed class Effect : Persistable {

        data class Error(val message: ResourceString) : Effect()

    }

}