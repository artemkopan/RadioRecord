package io.shared.presentation.player

import io.shared.core.Persistable
import io.shared.model.ResourceString
import io.shared.mvi.MviView
import io.shared.presentation.player.PlayerView.*
import kotlin.time.Duration
import kotlin.time.seconds


interface PlayerView : MviView<Intent, Model, Effect> {

    sealed class Intent {

        object PlayPause : Intent()
        object PlayNext : Intent()
        object PlayPrevious : Intent()
        object SlipForward : Intent()
        object SlipRewind : Intent()

        data class FindPosition(val position: Duration, val isScrubbing: Boolean) : Intent()

    }

    data class Model(
        val title: String = "",
        val subTitle: String = "",
        val cover: String = "",
        val isNextAvailable: Boolean = false,
        val isPreviousAvailable: Boolean = false,
        val isSeekingAvailable: Boolean = false,
        val isFastForwardAvailable: Boolean = false,
        val isRewindAvailable: Boolean = false,
        val currentDuration: Duration = 0.seconds,
        val currentDurationFormatted: String = "",
        val totalDuration: Duration = 0.seconds,
        val totalDurationFormatted: String = "",
        val isLoading: Boolean = false,
        val isPlaying: Boolean = false,
        val slip: Slip? = null
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