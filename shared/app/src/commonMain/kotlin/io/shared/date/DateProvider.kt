package io.shared.date

import kotlin.time.Duration

expect class DateProvider {

    val currentTime: Duration

    fun formatSec(duration: Duration): String

}