package io.shared.mapper

import org.koin.dsl.module


val mappersModule = module {

    factory { RadioPodcastDetailsItemMapper() }
    factory { RadioPodcastDetailsMapper(get()) }
    factory { RadioPodcastMapper() }
    factory { RadioStationMapper() }
    factory { TrackItemFromRadioPodcastMapper() }
    factory { TrackItemFromRadioStationMapper() }

}