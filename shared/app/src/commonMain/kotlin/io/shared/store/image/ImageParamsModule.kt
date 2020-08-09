package io.shared.store.image

import org.koin.dsl.module

val imageModule = module {

    factory { ImageParamsStoreFactory(get()) }
    factory { GetImageParamsByUrlMiddleware(get()) }

}