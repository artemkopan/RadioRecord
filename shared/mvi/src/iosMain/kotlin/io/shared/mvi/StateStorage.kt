package io.shared.mvi

actual class StateStorage {

    private val map = mutableMapOf<String, Any>()

    actual operator fun <T> get(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return this.map[key] as T?
    }

    actual operator fun <T> set(key: String, value: T?) {
        map[key] = value as Any
    }

}