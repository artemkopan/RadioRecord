package io.radio.shared.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.radio.shared.SystemConfig
import io.radio.shared.common.Inject

class AndroidNetworkConfiguration @Inject constructor(systemConfig: SystemConfig) :
    NetworkConfiguration,
    BaseNetworkConfiguration<HttpClientEngineFactory<OkHttpConfig>, OkHttpConfig>(systemConfig) {

    override fun initializeEngine(): HttpClientEngineFactory<OkHttpConfig> = OkHttp

}