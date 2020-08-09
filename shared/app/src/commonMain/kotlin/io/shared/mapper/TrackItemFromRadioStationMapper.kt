package io.shared.mapper

import io.shared.core.Mapper
import io.shared.core.toOptional
import io.shared.model.CoverImage
import io.shared.model.Station
import io.shared.model.TrackItem
import io.shared.model.TrackSource
import kotlin.time.milliseconds

class TrackItemFromRadioStationMapper : Mapper<TrackItem, Station>() {

    override fun map(from: Station, params: Any?): TrackItem = with(from) {
        TrackItem(
            from.id,
            from.title,
            "",
            CoverImage(from.icon).toOptional(),
            0.0.milliseconds,
            //todo check hls
            TrackSource.ProgressiveStream(
                when {
                    stream128.isNotEmpty() -> stream128
                    stream320.isNotEmpty() -> stream320
                    stream64.isNotEmpty() -> stream64
                    else -> stream32
                }
            )
        )
    }

}

