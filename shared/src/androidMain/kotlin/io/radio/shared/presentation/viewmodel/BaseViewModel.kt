package io.radio.shared.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class BaseViewModel : ViewModel() {

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }





}