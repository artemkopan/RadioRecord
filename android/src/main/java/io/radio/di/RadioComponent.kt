package io.radio.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import io.radio.presentation.MainActivity
import io.radio.presentation.stations.StationsFragment
import javax.inject.Singleton

@Component(modules = [RadioProvidersModule::class, RadioBindsModule::class, ViewModelsModule::class, AssistedViewModelsModule::class])
@Singleton
interface RadioComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: StationsFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun resources(resources: Resources): Builder

        fun build(): RadioComponent
    }

}


