package io.radio.shared.mapper

import io.radio.shared.common.Inject
import io.radio.shared.common.Mapper
import io.radio.shared.model.RadioStation
import io.radio.shared.network.reponse.RadioStationResponse


class RadioStationMapper @Inject constructor() : Mapper<RadioStation, RadioStationResponse>() {
    override fun map(from: RadioStationResponse, params: Any?): RadioStation = with(from) {
        RadioStation(
            title.orEmpty(),
            iconPng.orEmpty(),
            icon.orEmpty(),
            stream.orEmpty(),
            stream32.orEmpty(),
            stream64.orEmpty(),
            stream128.orEmpty(),
            stream320.orEmpty()
        )
    }
}