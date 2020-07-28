package io.radio.shared.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.base.extensions.orZero
import io.radio.shared.model.Podcast
import io.radio.shared.network.reponse.RadioPodcastResponse

class RadioPodcastMapper : Mapper<Podcast, RadioPodcastResponse>() {
    override fun map(from: RadioPodcastResponse, params: Any?): Podcast = with(from) {
        Podcast(
            from.id.orZero,
            from.name.orEmpty(),
            from.cover.orEmpty()
        )
    }

}