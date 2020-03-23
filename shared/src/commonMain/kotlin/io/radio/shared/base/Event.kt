@file:Suppress("NOTHING_TO_INLINE", "unused")

package io.radio.shared.base

import kotlin.jvm.Volatile

/**
 * Used as a wrapper for data that is exposed via a Stream that represents an event.
 */
open class Event<out T>(private val content: T) {

    @Volatile
    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    inline fun performContentIfNotHandled(crossinline block: (T) -> Unit) {
        getContentIfNotHandled()?.let(block)
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

}

inline fun <T> T.toEvent() = Event(this)