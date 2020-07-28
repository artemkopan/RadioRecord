package io.radio.shared.mapper

import io.radio.shared.base.Mapper
import io.radio.shared.base.extensions.orZero
import io.radio.shared.model.RadioPodcastDetailsItem
import io.radio.shared.network.reponse.RadioPodcastDetailsItemResponse

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
                link.orEmpty().replace("radioreord", "radiorecord") //temp solution due to bug on backend
            )
        }

}