package io.shared.mapper

import io.radio.shared.network.reponse.RadioPodcastResponse
import io.shared.core.Mapper
import io.shared.core.extensions.orZero
import io.shared.model.Podcast

class RadioPodcastMapper : Mapper<Podcast, RadioPodcastResponse>() {
    override fun map(from: RadioPodcastResponse, params: Any?): Podcast = with(from) {
        Podcast(
            from.id.orZero,
            from.name.orEmpty(),
            from.cover.orEmpty()
        )
    }

}