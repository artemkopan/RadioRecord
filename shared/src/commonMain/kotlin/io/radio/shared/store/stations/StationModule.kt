package io.radio.shared.store.stations

import org.koin.dsl.module

val stationModule = module {

    factory { LoadStationBootstrapper() }
    factory { LoadStationMiddleware(get()) }
    factory { PlayStationMiddleware(get(),get()) }

    factory { StationStoreFactory(get(), get(),get()) }

}