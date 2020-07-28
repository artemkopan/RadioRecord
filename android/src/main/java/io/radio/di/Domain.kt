package io.radio.di

import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.domain.usecases.track.TrackSeekUseCase
import io.radio.shared.domain.usecases.track.TrackUpdatePositionUseCase
import io.radio.shared.feature.date.AndroidDateProvider
import io.radio.shared.feature.date.DateProvider
import io.radio.shared.feature.image.ImageProcessor
import io.radio.shared.feature.image.ImageProcessorImpl
import io.radio.shared.formatters.TrackFormatter
import io.radio.shared.formatters.TrackFormatterImpl
import org.koin.dsl.module

val domainModule = module {

    single<TrackFormatter> { TrackFormatterImpl() }
    single<ImageProcessor> { ImageProcessorImpl(get()) }
    single<DateProvider> { AndroidDateProvider(get()) }

    factory { TrackMediaInfoCreatorUseCase(get()) }
    factory { TrackMediaInfoProcessUseCase(get()) }
    factory { TrackUpdatePositionUseCase(get()) }
    factory { TrackSeekUseCase(get(), get()) }

}