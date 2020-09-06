package io.shared.mvi

expect class StateStorage {

    operator fun <T> get(key: String): T?

    operator fun <T> set(key: String, value: T?)

}

inline fun <T> StateStorage.getOrDefault(key: String, crossinline default: () -> T): T {
    return get<T>(key) ?: default()
}