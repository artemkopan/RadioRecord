package io.shared.date

import android.content.Context
import kotlin.time.Duration
import kotlin.time.milliseconds

actual class DateProvider(private val context: Context) {

    actual val currentTime: Duration
        get() = System.currentTimeMillis().milliseconds

}