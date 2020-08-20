@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.shared.configs.SystemConfig
import kotlinx.serialization.json.Json

interface HttpClientProvider {

    val httpClient: HttpClient

    val urlBuilder: URLBuilder

}

class HttpClientProviderImpl(
    private val systemConfig: SystemConfig,
    private val httpEngineProvider: HttpEngineProvider
) : HttpClientProvider {

    override val httpClient: HttpClient by lazy {
        HttpClient(httpEngineProvider.provideEngine()) {
            install(JsonFeature) {
                acceptContentTypes = listOf(ContentType.Any)
                serializer = KotlinxSerializer(
                    Json(builderAction = {
                        ignoreUnknownKeys = true
                        useArrayPolymorphism = true
                    })
                )
            }

            if (systemConfig.isNetworkLogsEnabled) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            io.shared.core.Logger.d(message, tag = "ApiClient")
                        }
                    }
                    level = LogLevel.ALL
                }
            }
        }
    }

    override val urlBuilder: URLBuilder
        get() = URLBuilder(API_URL)

    companion object {
        private const val API_URL = "http://app-api.radiorecord.ru"
    }
}
