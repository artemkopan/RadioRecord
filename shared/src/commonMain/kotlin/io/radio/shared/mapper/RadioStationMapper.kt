package io.radio.shared.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.model.Station
import io.radio.shared.network.reponse.RadioStationResponse

class RadioStationMapper : Mapper<Station, RadioStationResponse>() {
    override fun map(from: RadioStationResponse, params: Any?): Station = with(from) {
        Station(
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