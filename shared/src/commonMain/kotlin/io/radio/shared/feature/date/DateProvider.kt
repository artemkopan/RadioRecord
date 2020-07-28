package io.radio.shared.feature.date

import kotlin.time.Duration

interface DateProvider {

    val currentTime: Duration

    fun formatSec(duration: Duration): String

}