package io.radio.di

enum class Qualifier {

    PlayerCoroutineQualifier

}

@Suppress("NOTHING_TO_INLINE")
inline fun Qualifier.named() = org.koin.core.qualifier.named(this)