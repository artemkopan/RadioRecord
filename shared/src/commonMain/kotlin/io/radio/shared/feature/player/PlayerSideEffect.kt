package io.radio.shared.feature.player

import io.radio.shared.base.Persistable

sealed class PlayerSideEffect : Persistable {

    data class Error(val throwable: Throwable) : PlayerSideEffect()

    data class SeekInScrubbing(val formattedCurrentTime: String) : PlayerSideEffect()

    sealed class Slip : PlayerSideEffect() {

        data class Rewind(val timeFormatted: String) : Slip()
        data class Forward(val timeFormatted: String) : Slip()

    }

}