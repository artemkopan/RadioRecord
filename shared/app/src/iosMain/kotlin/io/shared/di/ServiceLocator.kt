package io.shared.di

import io.shared.mvi.StateStorage
import io.shared.presentation.stations.StationViewBinder
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class ServiceLocator {

    private val di by DI.lazy {
        importAll(commonModules)
    }

//    private val koin = startKoin {
////        logger(KoinLogger())
//        modules(commonModules)
//    }.koin

    fun getStationViewBinder(): StationViewBinder {
//        return koin.get(parameters = { parametersOf(StateStorage()) })
//        val radioApiSource = RadioApiSourceImpl(
//            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
//            RadioStationMapper(),
//            RadioPodcastMapper(),
//            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
//        )
//        val radioRepository = RadioRepositoryImpl(radioApiSource)
//
//        return StationViewBinder(
//            StateStorage(), StationStoreFactory(
//                LoadStationMiddleware(radioRepository),
//                PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
//            ), ErrorFormatter()
//        )
        return di.direct.instance(arg = StateStorage())
    }

}