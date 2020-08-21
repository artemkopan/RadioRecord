package io.shared.mapper

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider


val mappersModule = DI.Module("mappers") {

    bind() from provider { RadioPodcastDetailsItemMapper() }
    bind() from provider { RadioPodcastDetailsMapper(instance()) }
    bind() from provider { RadioPodcastMapper() }
    bind() from provider { RadioStationMapper() }
    bind() from provider { TrackItemFromRadioPodcastMapper() }
    bind() from provider { TrackItemFromRadioStationMapper() }

}