package io.radio.di

import dagger.Binds
import dagger.Module
import io.radio.shared.SystemConfig
import io.radio.shared.SystemConfigImpl
import io.radio.shared.network.AndroidNetworkConfiguration
import io.radio.shared.network.ApiSource
import io.radio.shared.network.ApiSourceImpl
import io.radio.shared.network.NetworkConfiguration
import io.radio.shared.repositories.station.RadioStationRepository
import io.radio.shared.repositories.station.RadioStationRepositoryImpl
import javax.inject.Singleton

@Module
abstract class RadioBindsModule {

    @Binds
    abstract fun bindRestApiService(impl: ApiSourceImpl): ApiSource

    @Binds
    @Singleton
    abstract fun bindNetworkConfiguration(impl: AndroidNetworkConfiguration): NetworkConfiguration

    @Binds
    @Singleton
    abstract fun bindSystemConfig(impl: SystemConfigImpl): SystemConfig

    @Binds
    @Singleton
    abstract fun bindRadioStationRepository(impl: RadioStationRepositoryImpl): RadioStationRepository
}