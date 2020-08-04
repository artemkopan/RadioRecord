package io.radio.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.radio.shared.configs.BaseNetworkConfiguration
import io.radio.shared.configs.NetworkConfiguration
import io.radio.shared.configs.SystemConfig

class AndroidNetworkConfiguration constructor(systemConfig: SystemConfig) :
    NetworkConfiguration,
    BaseNetworkConfiguration<HttpClientEngineFactory<OkHttpConfig>, OkHttpConfig>(systemConfig) {

    override fun initializeEngine(): HttpClientEngineFactory<OkHttpConfig> = OkHttp

}