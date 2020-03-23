package io.radio.shared.data.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.base.extensions.orZero
import io.radio.shared.data.network.reponse.RadioPodcastDetailsItemResponse
import io.radio.shared.model.RadioPodcastDetailsItem

class RadioPodcastDetailsItemMapper constructor() :
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