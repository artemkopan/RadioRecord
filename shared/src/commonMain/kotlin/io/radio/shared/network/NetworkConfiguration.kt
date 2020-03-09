package io.radio.shared.network

import io.ktor.client.HttpClient

interface NetworkConfiguration {

    val httpClient: HttpClient

}

