package io.radio.shared.mapper

import io.radio.shared.common.Inject
import io.radio.shared.common.Mapper
import io.radio.shared.common.orZero
import io.radio.shared.model.RadioPodcast
import io.radio.shared.network.reponse.RadioPodcastResponse

class RadioPodcastMapper @Inject constructor(): Mapper<RadioPodcast, RadioPodcastResponse>() {
    override fun map(from: RadioPodcastResponse, params: Any?): RadioPodcast = with(from) {
        RadioPodcast(
            from.id.orZero,
            from.name.orEmpty(),
            from.cover.orEmpty()
        )
    }

}