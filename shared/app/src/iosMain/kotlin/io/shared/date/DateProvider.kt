package io.shared.date

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

actual class DateProvider {

    actual val currentTime: Duration
        get() = NSDate().timeIntervalSince1970.toDuration(DurationUnit.SECONDS)

}