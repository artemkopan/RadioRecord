package io.shared.di

import io.shared.configs.SystemConfigImpl
import io.shared.formatters.ErrorFormatter
import io.shared.mapper.*
import io.shared.mvi.StateStorage
import io.shared.network.HttpClientProviderImpl
import io.shared.network.HttpEngineProvider
import io.shared.network.RadioApiSourceImpl
import io.shared.presentation.stations.StationViewBinder
import io.shared.repo.RadioRepositoryImpl
import io.shared.store.player.MediaPlayer
import io.shared.store.stations.LoadStationMiddleware
import io.shared.store.stations.PlayStationMiddleware
import io.shared.store.stations.StationStoreFactory

class ServiceLocator {

//    private val koin = startKoin {
////        logger(KoinLogger())
//        modules(commonModules)
//    }.koin

    fun getStationViewBinder(): StationViewBinder {
//        return koin.get(parameters = { parametersOf(StateStorage()) })
        val radioApiSource = RadioApiSourceImpl(
            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
            RadioStationMapper(),
            RadioPodcastMapper(),
            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
        )
        val radioRepository = RadioRepositoryImpl(radioApiSource)

        return StationViewBinder(
            StateStorage(), StationStoreFactory(
                LoadStationMiddleware(radioRepository),
                PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
            ), ErrorFormatter()
        )
    }

}