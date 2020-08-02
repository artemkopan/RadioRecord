package io.radio.shared.presentation.stations

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.MviView
import io.radio.shared.base.mvi.ViewEvent
import io.radio.shared.model.Station
import io.radio.shared.presentation.stations.StationView.*

interface StationView : MviView<Intent, Model, Event> {

    sealed class Intent {

        data class SelectStation(val station: Station) : Intent()

    }

    data class Model(val isLoading: Boolean, val data: List<Station>) : Persistable

    sealed class Event : ViewEvent {

        data class Error(val message: String, override val tag: String) : Event()

        object NavigateToPlayer : Event() {
            override val tag: String get() = "navigate-to-player"
        }

    }

}