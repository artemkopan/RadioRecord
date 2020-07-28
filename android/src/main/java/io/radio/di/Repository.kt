package io.radio.di

import io.radio.shared.feature.radio.RadioRepository
import io.radio.shared.feature.radio.RadioRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<RadioRepository> { RadioRepositoryImpl(get()) }

}