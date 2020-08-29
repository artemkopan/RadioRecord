@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.shared.network

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
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
                //TODO bug with Logging on ktor version 1.3.9 https://youtrack.jetbrains.com/issue/KTOR-924
//                install(Logging) {
//                    logger = object : Logger {
//                        override fun log(message: String) {
//                            io.shared.core.Logger.d(message, tag = "ApiClient")
//                        }
//                    }
//                    level = LogLevel.ALL
//                }
            }
        }
    }

    override val urlBuilder: URLBuilder
        get() = URLBuilder(API_URL)

    companion object {
        private const val API_URL = "http://app-api.radiorecord.ru"
    }
}
