package io.shared.mvi

import androidx.lifecycle.SavedStateHandle

actual class StateStorage(private val savedStateHandle: SavedStateHandle) {

    actual operator fun <T> get(key: String): T? {
        return savedStateHandle.get(key)
    }

    actual operator fun <T> set(key: String, value: T?) {
        savedStateHandle.set(key, value)
    }

}