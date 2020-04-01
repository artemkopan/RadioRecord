package io.radio.di

import io.radio.shared.domain.date.AndroidDateProvider
import io.radio.shared.domain.date.DateProvider
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.formatters.TrackFormatterImpl
import io.radio.shared.domain.image.ImageProcessor
import io.radio.shared.domain.image.ImageProcessorImpl
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.domain.usecases.track.TrackSeekUseCase
import io.radio.shared.domain.usecases.track.TrackUpdatePositionUseCase
import org.koin.dsl.module

val domainModule = module {

    single<TrackFormatter> { TrackFormatterImpl() }
    single<ImageProcessor> { ImageProcessorImpl(get()) }
    single<DateProvider> { AndroidDateProvider(get()) }

    factory { TrackMediaInfoCreatorUseCase(get()) }
    factory { TrackMediaInfoProcessUseCase(get()) }
    factory { TrackUpdatePositionUseCase(get(), get()) }
    factory { TrackSeekUseCase(get(), get()) }

}