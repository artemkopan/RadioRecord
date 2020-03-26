package io.radio.di

import io.radio.shared.domain.repositories.station.RadioRepository
import io.radio.shared.domain.repositories.station.RadioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> { RadioRepositoryImpl(get()) }

}