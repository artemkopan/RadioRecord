package io.radio.shared.feature.player

import io.radio.shared.base.Persistable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class PlayerState(
    val isLoading: Boolean = false,
    val isPlay: Boolean = false,
    val isNextAvailable: Boolean = false,
    val isPreviousAvailable: Boolean = false,
    val logo: String = "",
    val title: String = "",
    val subTitle: String = "",
    val currentDuration: Duration = 0.0.toDuration(DurationUnit.MILLISECONDS),
    val totalDuration: Duration = 0.0.toDuration(DurationUnit.MILLISECONDS)
) : Persistable