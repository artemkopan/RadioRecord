package io.radio.shared.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.model.PodcastDetails
import io.radio.shared.network.reponse.RadioPodcastDetailsResponse

class RadioPodcastDetailsMapper constructor(private val radioPodcastDetailsItemMapper: RadioPodcastDetailsItemMapper) :
    Mapper<PodcastDetails, RadioPodcastDetailsResponse>() {

    override fun map(from: RadioPodcastDetailsResponse, params: Any?): PodcastDetails =
        with(from) {
            PodcastDetails(
                name.orEmpty(),
                cover.orEmpty(),
                radioPodcastDetailsItemMapper.mapList(items.orEmpty())
            )
        }

}