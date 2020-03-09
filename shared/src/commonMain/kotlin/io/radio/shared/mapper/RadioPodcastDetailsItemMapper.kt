package io.radio.shared.mapper

import io.radio.shared.common.Inject
import io.radio.shared.common.Mapper
import io.radio.shared.common.orZero
import io.radio.shared.model.RadioPodcastDetailsItem
import io.radio.shared.network.reponse.RadioPodcastDetailsItemResponse

class RadioPodcastDetailsItemMapper @Inject constructor() :
    Mapper<RadioPodcastDetailsItem, RadioPodcastDetailsItemResponse>() {

    override fun map(from: RadioPodcastDetailsItemResponse, params: Any?): RadioPodcastDetailsItem =
        with(from) {
            RadioPodcastDetailsItem(
                id.orZero,
                time.orZero,
                title.orEmpty(),
                artist.orEmpty(),
                song.orEmpty(),
                playlist.orEmpty(),
                link.orEmpty()
            )
        }

}