@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.radio.shared.domain.configs

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

interface NetworkConfiguration {

    val httpClient: HttpClient

}

abstract class BaseNetworkConfiguration<out TEngine, TConfig>(private val systemConfig: SystemConfig) :
    NetworkConfiguration where TEngine : HttpClientEngineFactory<TConfig>, TConfig : HttpClientEngineConfig {

    abstract fun initializeEngine(): TEngine

    override val httpClient: HttpClient by lazy {
        HttpClient(initializeEngine()) {

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
                            io.radio.shared.base.Logger.d("ApiClient", message)
                        }
                    }

                    level = LogLevel.ALL
                }
            }
        }
    }


}
