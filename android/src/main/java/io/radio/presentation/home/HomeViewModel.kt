package io.radio.presentation.home

import io.radio.shared.common.Inject
import io.radio.shared.presentation.viewmodel.BaseViewModel
import io.radio.shared.repositories.station.RadioStationRepository

class HomeViewModel @Inject constructor(
    radioStationRepository: RadioStationRepository
) : BaseViewModel() {

    init {
    }


}