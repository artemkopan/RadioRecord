package io.radio.di

import io.radio.shared.repo.RadioRepository
import io.radio.shared.repo.RadioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> {
        RadioRepositoryImpl(
            get()
        )
    }

}