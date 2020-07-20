package io.radio.shared.feature.player

import io.radio.shared.base.Event

sealed class PlayerEvents {

    data class Error(val event: Event<Throwable>) : PlayerEvents()

    sealed class Seek {

        object None : Seek()
        data class Rewind(val timeFormatted: String) : Seek()
        data class Forward(val timeFormatted: String) : Seek()

    }

}