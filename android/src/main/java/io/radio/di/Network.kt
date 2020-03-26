package io.radio.di

import io.radio.shared.data.network.ApiSource
import io.radio.shared.data.network.ApiSourceImpl
import io.radio.shared.domain.configs.NetworkConfiguration
import io.radio.shared.network.AndroidNetworkConfiguration
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