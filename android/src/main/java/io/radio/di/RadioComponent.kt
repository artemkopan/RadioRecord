package io.radio.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import io.radio.MainActivity
import javax.inject.Singleton

@Component(modules = [RadioProvidersModule::class, RadioBindsModule::class])
@Singleton
interface RadioComponent {

    fun inject(activity: MainActivity)

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


