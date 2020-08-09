package io.shared.mvi

expect class StateStorage {

    operator fun <T> get(key: String): T?

    operator fun <T> set(key: String, value: T?)

}