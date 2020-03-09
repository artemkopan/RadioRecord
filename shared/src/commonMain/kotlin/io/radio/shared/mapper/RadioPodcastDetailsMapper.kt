package io.radio.shared.mapper

import io.radio.shared.common.Inject
import io.radio.shared.common.Mapper
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.network.reponse.RadioPodcastDetailsResponse

class RadioPodcastDetailsMapper @Inject constructor(private val radioPodcastDetailsItemMapper: RadioPodcastDetailsItemMapper) :
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