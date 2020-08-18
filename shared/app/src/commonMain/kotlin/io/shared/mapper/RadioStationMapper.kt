package io.shared.mapper

import io.shared.core.Mapper
import io.shared.model.Station
import io.shared.network.reponse.RadioStationResponse

class RadioStationMapper : Mapper<Station, RadioStationResponse>() {
    override fun map(from: RadioStationResponse, params: Any?): Station = with(from) {
        Station(
            prefix!!.hashCode(),
            title.orEmpty(),
            iconGray.orEmpty(),
            iconFillWhite.orEmpty(),
            stream64.orEmpty(),
            stream128.orEmpty(),
            stream320.orEmpty()
        )
    }
}