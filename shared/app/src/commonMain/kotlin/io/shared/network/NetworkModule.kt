package io.shared.network

import org.koin.dsl.module

val networkModule = module {

    single<RadioApiSource> {
        RadioApiSourceImpl(
            get(),
            get(),
            get(),
            get()
        )
    }
    single<HttpClientProvider> { HttpClientProviderImpl(get(), get()) }

}