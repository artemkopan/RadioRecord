package io.shared.date

import android.content.Context
import io.shared.app.R
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.milliseconds

actual class DateProvider(private val context: Context) {

    actual val currentTime: Duration
        get() = System.currentTimeMillis().milliseconds

    actual fun formatSec(duration: Duration): String {
        return context.getString(
            R.string.timeFormatSeconds,
            duration.toLong(DurationUnit.SECONDS)
        )
    }

}