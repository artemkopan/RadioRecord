@file:Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "unused", "NOTHING_TO_INLINE")

package io.radio.shared.base

import kotlinx.serialization.Serializable

@Serializable
class Optional<T> private constructor(val data: T?) {

    companion object {
        private val OPTIONAL_EMPTY = Optional(null)

        fun <T> empty(): Optional<T> = OPTIONAL_EMPTY as Optional<T>
        fun <T> of(t: T?): Optional<T> = if (t == null) empty() else Optional(
            t
        )
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Optional<*>) {
            if (data == null && other.data == null) {
                true
            } else {
                data == other.data
            }
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return data?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Optional [$data]"
    }
}


inline fun <T> Optional<T>.isEmpty() = data == null
inline fun <T> Optional<T>.isNotEmpty() = !isEmpty()

inline fun <T> Optional<T>.get(crossinline default: () -> T) = data ?: default()

inline fun <T, M> Optional<T>.getAndMap(
    crossinline transform: (T) -> M,
    crossinline default: () -> M
) = data?.let(transform) ?: default()

inline fun <T> Optional<T>.getOrThrow(crossinline message: () -> String = { "Data is null" }) =
    data ?: throw NullPointerException(message())

inline fun <T> T?.toOptional() =
    Optional.of(this)

inline fun <T> Optional<T>.fold(onResult: (T) -> Unit, onEmpty: () -> Unit) {
    if (isNotEmpty()) {
        onResult(data!!)
    } else {
        onEmpty()
    }
}