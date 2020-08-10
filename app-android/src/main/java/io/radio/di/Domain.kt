package io.radio.di

//import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
//import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
//import io.radio.shared.domain.usecases.track.TrackSeekUseCase
//import io.radio.shared.domain.usecases.track.TrackUpdatePositionUseCase
import io.shared.date.DateProvider
import io.shared.formatters.TrackFormatter
import io.shared.formatters.TrackFormatterImpl
import io.shared.image.ImageProcessor
import io.shared.image.ImageProcessorImpl
import org.koin.dsl.module

val domainModule = module {

    single<TrackFormatter> { TrackFormatterImpl() }
    single<ImageProcessor> { ImageProcessorImpl(get()) }
    single { DateProvider(get()) }

//    factory { TrackMediaInfoCreatorUseCase(get()) }
//    factory { TrackMediaInfoProcessUseCase(get()) }
//    factory { TrackUpdatePositionUseCase(get()) }
//    factory { TrackSeekUseCase(get(), get()) }

}