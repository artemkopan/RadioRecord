package io.radio.di

import io.radio.data.AppResourcesImpl
import io.radio.shared.data.AppResources
import io.radio.shared.data.SystemConfig
import io.radio.shared.data.SystemConfigImpl
import io.radio.shared.data.image.ImageProcessor
import io.radio.shared.data.image.ImageProcessorImpl
import org.koin.dsl.module

val dataModule = module {

    single<SystemConfig> { SystemConfigImpl() }
    single<AppResources> { AppResourcesImpl(get()) }
    single<ImageProcessor> { ImageProcessorImpl(get()) }

}