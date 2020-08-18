package io.shared.mapper

import io.shared.core.Mapper
import io.shared.core.extensions.orZero
import io.shared.model.RadioPodcastDetailsItem
import io.shared.network.reponse.RadioPodcastDetailsItemResponse

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
                link.orEmpty(),
                image100.orEmpty(),
                image600.orEmpty()
            )
        }

}