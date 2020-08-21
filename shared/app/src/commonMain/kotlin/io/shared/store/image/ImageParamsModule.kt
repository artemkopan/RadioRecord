package io.shared.store.image

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider


val imageModule = DI.Module("image-processing") {

    bind() from provider { ImageParamsStoreFactory(instance()) }
    bind() from provider { GetImageParamsByUrlMiddleware(instance()) }

}