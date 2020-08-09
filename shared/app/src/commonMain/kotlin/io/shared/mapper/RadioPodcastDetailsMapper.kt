package io.shared.mapper

import io.shared.core.Mapper
import io.shared.model.PodcastDetails
import io.shared.network.reponse.RadioPodcastDetailsResponse

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