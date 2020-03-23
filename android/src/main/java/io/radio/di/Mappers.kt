package io.radio.di

import io.radio.shared.data.mapper.RadioPodcastDetailsItemMapper
import io.radio.shared.data.mapper.RadioPodcastDetailsMapper
import io.radio.shared.data.mapper.RadioPodcastMapper
import io.radio.shared.data.mapper.RadioStationMapper
import org.koin.dsl.module


val mappersModule = module {

    factory { RadioPodcastDetailsItemMapper() }
    factory { RadioPodcastDetailsMapper(get()) }
    factory { RadioPodcastMapper() }
    factory { RadioStationMapper() }
}