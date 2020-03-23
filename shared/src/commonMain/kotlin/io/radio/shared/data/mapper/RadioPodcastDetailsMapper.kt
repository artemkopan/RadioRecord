package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.data.network.reponse.RadioPodcastDetailsResponse
import io.radio.shared.model.RadioPodcastDetails

class RadioPodcastDetailsMapper constructor(private val radioPodcastDetailsItemMapper: RadioPodcastDetailsItemMapper) :
    Mapper<RadioPodcastDetails, RadioPodcastDetailsResponse>() {

    override fun map(from: RadioPodcastDetailsResponse, params: Any?): RadioPodcastDetails =
        with(from) {
            RadioPodcastDetails(
                name.orEmpty(),
                cover.orEmpty(),
                radioPodcastDetailsItemMapper.mapList(items.orEmpty())
            )
        }

}