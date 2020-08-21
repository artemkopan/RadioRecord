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
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class ServiceLocator {

    private val di by DI.lazy {
        importAll(commonModules)
    }

    fun getStationViewBinder(): StationViewBinder {
//        val radioApiSource = RadioApiSourceImpl(
//            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
//            RadioStationMapper(),
//            RadioPodcastMapper(),
//            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
//        )
//        val radioRepository = RadioRepositoryImpl(radioApiSource)
//Dispatchers.Main
//        return StationViewBinder(
//            StateStorage(), StationStoreFactory(
//                LoadStationMiddleware(radioRepository),
//                PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
//            ), ErrorFormatter()
//        )
        return di.direct.instance(arg = StateStorage())
    }

}