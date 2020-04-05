package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.data.network.reponse.RadioStationResponse
import io.radio.shared.model.RadioStation

class RadioStationMapper : Mapper<RadioStation, RadioStationResponse>() {
    override fun map(from: RadioStationResponse, params: Any?): RadioStation = with(from) {
        RadioStation(
            prefix!!.hashCode(),
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