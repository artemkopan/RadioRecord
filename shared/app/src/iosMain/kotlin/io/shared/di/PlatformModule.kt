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
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual val platformModule = DI.Module("platform") {

    bind<DateProvider>() with singleton { DateProvider() }
    bind<SystemConfig>() with singleton { SystemConfigImpl() }

    bind<MediaPlayer>() with singleton { MediaPlayer() }
    bind<ImageProcessor>() with singleton { ImageProcessor() }

    bind<ErrorFormatter>() with singleton { ErrorFormatter() }
    bind<TrackFormatter>() with singleton { TrackFormatterImpl() }

    bind<HttpEngineProvider>() with singleton { HttpEngineProvider() }

}