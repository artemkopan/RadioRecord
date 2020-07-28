package io.radio.shared.formatters

import kotlin.time.Duration

interface TrackFormatter {

    fun formatDuration(duration: Duration): String

}

class TrackFormatterImpl : TrackFormatter {
    override fun formatDuration(duration: Duration): String {
        return duration.toComponents { hours, minutes, seconds, _ ->
            "${
            if (hours == 0) "" else hours.toString().padStart(2, PAD_CHAR) + SEP_CHAR
            }${
            minutes.toString().padStart(2, PAD_CHAR)
            }${SEP_CHAR}${
            seconds.toString().padStart(2, PAD_CHAR)
            }"
        }
    }

    private companion object {
        private const val PAD_CHAR = '0'
        private const val SEP_CHAR = ':'
    }
}