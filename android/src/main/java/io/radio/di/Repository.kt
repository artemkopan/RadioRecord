package io.radio.di

import io.radio.shared.data.repositories.station.RadioRepository
import io.radio.shared.data.repositories.station.RadioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> { RadioRepositoryImpl(get()) }

}