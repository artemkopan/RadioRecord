package io.shared.repo

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val repositoryModule = DI.Module("repository") {

    bind<RadioRepository>() with provider { RadioRepositoryImpl(instance()) }

}