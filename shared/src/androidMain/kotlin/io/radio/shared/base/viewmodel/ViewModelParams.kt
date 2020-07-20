package io.radio.shared.base.viewmodel

import androidx.lifecycle.SavedStateHandle


class SavedStateStorage(private val savedStateHandle: SavedStateHandle) : StateStorage {
    override fun <T> get(key: String): T? = savedStateHandle.get<T>(key)
    override fun <T> set(key: String, value: T?) {
        savedStateHandle.set(key, value)
    }
}