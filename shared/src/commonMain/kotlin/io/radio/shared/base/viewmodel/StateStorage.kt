package io.radio.shared.base.viewmodel

interface StateStorage {

    operator fun <T> get(key: String): T?

    operator fun <T> set(key: String, value: T?)

}