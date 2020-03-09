package io.radio.di

import dagger.Binds
import dagger.Module
import io.radio.shared.SystemConfig
import io.radio.shared.SystemConfigImpl
import io.radio.shared.network.AndroidNetworkConfiguration
import io.radio.shared.network.NetworkConfiguration
import io.radio.shared.network.RestApiService
import io.radio.shared.network.RestApiServiceImpl
import javax.inject.Singleton

@Module
abstract class RadioBindsModule {

    @Binds
    abstract fun bindRestApiService(impl: RestApiServiceImpl): RestApiService

    @Binds
    @Singleton
    abstract fun bindNetworkConfiguration(impl: AndroidNetworkConfiguration): NetworkConfiguration

    @Binds
    @Singleton
    abstract fun bindSystemConfig(impl: SystemConfigImpl): SystemConfig
}