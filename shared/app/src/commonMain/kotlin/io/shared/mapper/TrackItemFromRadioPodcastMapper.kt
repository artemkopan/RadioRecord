package io.shared.mapper

import io.shared.core.Mapper
import io.shared.core.toOptional
import io.shared.model.CoverImage
import io.shared.model.RadioPodcastDetailsItem
import io.shared.model.TrackItem
import io.shared.model.TrackSource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TrackItemFromRadioPodcastMapper : Mapper<TrackItem, RadioPodcastDetailsItem>() {
    override fun map(from: RadioPodcastDetailsItem, params: Any?): TrackItem = with(from) {
        TrackItem(
            from.id,
            from.artist,
            from.song,
            CoverImage(from.image600.ifEmpty { (params as? CoverImage)?.img ?: "" }).toOptional(),
            time.toDuration(DurationUnit.SECONDS),
            TrackSource.Progressive(from.link)
        )
    }

}

