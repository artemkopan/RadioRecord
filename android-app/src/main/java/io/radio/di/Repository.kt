package io.radio.di

import io.shared.repo.RadioRepository
import io.shared.repo.RadioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> {
        RadioRepositoryImpl(
            get()
        )
    }

}