package io.shared.date

import kotlin.time.Duration
import kotlin.time.milliseconds

actual class DateProvider() {

    actual val currentTime: Duration
        get() = System.currentTimeMillis().milliseconds

}