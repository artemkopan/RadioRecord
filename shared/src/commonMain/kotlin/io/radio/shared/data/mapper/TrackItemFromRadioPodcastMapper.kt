package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.model.RadioPodcastDetailsItem
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackSource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TrackItemFromRadioPodcastMapper : Mapper<TrackItem, RadioPodcastDetailsItem>() {
    override fun map(from: RadioPodcastDetailsItem, params: Any?): TrackItem = with(from) {
        TrackItem(
            from.id,
            from.title,
            from.song,
            time.toDuration(DurationUnit.SECONDS),
            TrackSource.Progressive(from.link)
        )
    }
}