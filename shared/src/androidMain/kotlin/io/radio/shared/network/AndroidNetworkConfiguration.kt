package io.radio.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.defaultSerializer
import io.radio.shared.SystemConfig
import io.radio.shared.common.Inject
import io.radio.shared.common.Logger
import okhttp3.logging.HttpLoggingInterceptor

class AndroidNetworkConfiguration @Inject constructor(private val systemConfig: SystemConfig) :
    NetworkConfiguration {

    override val httpClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = defaultSerializer()
            }
            engine {
                addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Logger.d("Okhttp", message)
                    }
                }).apply {
                    level = if (systemConfig.isNetworkLogsEnabled) {
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                })
            }
            //todo add okhttp interceptor
        }
    }

}