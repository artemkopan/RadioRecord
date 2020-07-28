package io.radio.shared.feature.player

import io.radio.shared.base.Persistable

data class PlayerState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val isNextAvailable: Boolean = false,
    val isPreviousAvailable: Boolean = false,
    val isSeekAvailable: Boolean = false,
    val logo: String = "",
    val title: String = "",
    val subTitle: String = "",
    val currentDuration: Int = 0,
    val currentDurationFormatted: String = "",
    val totalDuration: Int = 0,
    val totalDurationFormatted: String = ""
) : Persistable