package io.shared.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual class HttpEngineProvider {

    actual fun provideEngine(): HttpClientEngine {
        return OkHttp.create {}
    }

}
