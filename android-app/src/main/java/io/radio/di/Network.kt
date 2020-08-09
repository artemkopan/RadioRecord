package io.radio.di

import io.shared.configs.NetworkConfiguration
import io.shared.network.AndroidNetworkConfiguration
import io.shared.network.ApiSource
import io.shared.network.ApiSourceImpl
import org.koin.dsl.module

val networkModule = module {

    single<ApiSource> {
        ApiSourceImpl(
            get(),
            get(),
            get(),
            get()
        )
    }
    single<NetworkConfiguration> { AndroidNetworkConfiguration(get()) }

}