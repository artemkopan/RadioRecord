package io.shared.core

interface Loggable {

    fun toLogMessage(): String

}

@Suppress("NOTHING_TO_INLINE")
inline fun Any?.getLogMessageOrDefault() = if (this is Loggable) toLogMessage() else toString()