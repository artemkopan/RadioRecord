package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.base.toOptional
import io.radio.shared.model.CoverImage
import io.radio.shared.model.RadioStation
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackSource
import kotlin.time.milliseconds

class TrackItemFromRadioStationMapper : Mapper<TrackItem, RadioStation>() {
    override fun map(from: RadioStation, params: Any?): TrackItem = with(from) {
        TrackItem(
            from.title.hashCode(),
            from.title,
            "",
            CoverImage(from.icon).toOptional(),
            0.0.milliseconds,
            //todo check hls
            TrackSource.Progressive(
                when {
                    stream320.isNotEmpty() -> stream320
                    stream128.isNotEmpty() -> stream128
                    stream64.isNotEmpty() -> stream64
                    else -> stream32
                }
            )
        )
    }

}

