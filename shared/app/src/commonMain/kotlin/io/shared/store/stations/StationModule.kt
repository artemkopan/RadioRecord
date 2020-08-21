package io.shared.store.stations

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val stationModule = DI.Module("station") {

    bind() from provider { LoadStationMiddleware(instance()) }
    bind() from provider { PlayStationMiddleware(instance(), instance()) }

    bind() from provider { StationStoreFactory(instance(), instance()) }

}