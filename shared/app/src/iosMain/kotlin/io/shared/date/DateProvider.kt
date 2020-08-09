package io.shared.date

import kotlin.time.Duration

actual class DateProvider {

    actual val currentTime: Duration
        get() = TODO("Not yet implemented")

    actual fun formatSec(duration: Duration): String {
        TODO("Not yet implemented")
    }
}