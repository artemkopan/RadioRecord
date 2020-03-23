package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.base.extensions.orZero
import io.radio.shared.data.network.reponse.RadioPodcastResponse
import io.radio.shared.model.RadioPodcast

class RadioPodcastMapper : Mapper<RadioPodcast, RadioPodcastResponse>() {
    override fun map(from: RadioPodcastResponse, params: Any?): RadioPodcast = with(from) {
        RadioPodcast(
            from.id.orZero,
            from.name.orEmpty(),
            from.cover.orEmpty()
        )
    }

}