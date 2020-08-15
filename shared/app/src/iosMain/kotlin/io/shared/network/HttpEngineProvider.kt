package io.shared.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual class HttpEngineProvider {

    actual fun provideEngine(): HttpClientEngine {
        return Ios.create {
            configureRequest {
                setAllowsCellularAccess(true)
            }
        }
    }

}