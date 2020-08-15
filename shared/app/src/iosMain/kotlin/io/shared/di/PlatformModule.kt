package io.shared.di

import io.shared.configs.SystemConfig
import io.shared.configs.SystemConfigImpl
import io.shared.date.DateProvider
import io.shared.formatters.ErrorFormatter
import io.shared.formatters.TrackFormatter
import io.shared.formatters.TrackFormatterImpl
import io.shared.image.ImageProcessor
import io.shared.network.HttpEngineProvider
import io.shared.store.player.MediaPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {

    single { DateProvider() }
    single<SystemConfig> { SystemConfigImpl() }

    single { MediaPlayer() }
    single { ImageProcessor() }

    single { ErrorFormatter() }
    single<TrackFormatter> { TrackFormatterImpl() }

    single { HttpEngineProvider() }

}