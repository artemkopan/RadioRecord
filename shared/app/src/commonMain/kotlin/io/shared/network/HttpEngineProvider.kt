package io.shared.network

import io.ktor.client.engine.*

expect class HttpEngineProvider {

   fun provideEngine() : HttpClientEngine

}