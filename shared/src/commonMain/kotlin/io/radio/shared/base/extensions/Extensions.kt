package io.radio.shared.base.extensions

val Int?.orZero: Int
    get() = this ?: 0

fun <T> lazyNonSafety(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun notImplemented(target: String): Nothing =
    throw NotImplementedError("Not implemented: $target")