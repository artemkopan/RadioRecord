package io.shared.repo

import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> { RadioRepositoryImpl(get()) }

}