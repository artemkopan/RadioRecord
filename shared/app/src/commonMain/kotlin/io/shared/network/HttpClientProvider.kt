@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.shared.network

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import io.shared.configs.SystemConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

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
                    Json(
                        JsonConfiguration(
                            ignoreUnknownKeys = true,
                            useArrayPolymorphism = true
                        )
                    )
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
        private const val API_URL = "http://www.radiorecord.ru"
    }
}
