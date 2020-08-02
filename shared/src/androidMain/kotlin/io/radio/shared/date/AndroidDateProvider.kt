package io.radio.shared.date

import android.content.Context
import io.radio.shared.R
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.milliseconds

class AndroidDateProvider(private val context: Context) :
    DateProvider {

    override val currentTime: Duration
        get() = System.currentTimeMillis().milliseconds

    override fun formatSec(duration: Duration): String {
        return context.getString(R.string.timeFormatSeconds, duration.toLong(DurationUnit.SECONDS))
    }
}