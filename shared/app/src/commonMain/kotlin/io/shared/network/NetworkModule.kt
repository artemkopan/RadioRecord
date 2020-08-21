package io.shared.network

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton


val networkModule = DI.Module("network") {

    bind<RadioApiSource>() with singleton {
        RadioApiSourceImpl(instance(), instance(), instance(), instance())
    }

    bind<HttpClientProvider>() with singleton { HttpClientProviderImpl(instance(), instance()) }

}