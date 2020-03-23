package io.radio.shared.base.viewmodel

import androidx.lifecycle.SavedStateHandle


class SavedStateViewModelParams(private val savedStateHandle: SavedStateHandle) : ViewModelParams {
    override fun <T> get(key: String): T? = savedStateHandle.get<T>(key)
    override fun <T> set(key: String, value: T?) {
        savedStateHandle.set(key, value)
    }
}