package io.radio.di

import io.radio.shared.configs.NetworkConfiguration
import io.radio.shared.network.AndroidNetworkConfiguration
import io.radio.shared.network.ApiSource
import io.radio.shared.network.ApiSourceImpl
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