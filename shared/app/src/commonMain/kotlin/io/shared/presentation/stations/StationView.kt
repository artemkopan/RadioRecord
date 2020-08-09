package io.shared.presentation.stations

import io.shared.core.Persistable
import io.shared.model.ResourceString
import io.shared.model.Station
import io.shared.mvi.MviView
import io.shared.presentation.stations.StationView.*

interface StationView : MviView<Intent, Model, Effect> {

    sealed class Intent {

        data class SelectStation(val station: Station) : Intent()

    }

    data class Model(
        val isLoading: Boolean,
        val data: List<Station>
    ) : Persistable

    sealed class Effect : Persistable {

        data class Error(val message: ResourceString) : Effect()

        object NavigateToPlayer : Effect()

    }

}