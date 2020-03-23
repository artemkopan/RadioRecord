package io.radio.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.radio.shared.data.SystemConfig
import io.radio.shared.data.network.BaseNetworkConfiguration
import io.radio.shared.data.network.NetworkConfiguration

class AndroidNetworkConfiguration constructor(systemConfig: SystemConfig) :
    NetworkConfiguration,
    BaseNetworkConfiguration<HttpClientEngineFactory<OkHttpConfig>, OkHttpConfig>(systemConfig) {

    override fun initializeEngine(): HttpClientEngineFactory<OkHttpConfig> = OkHttp

}