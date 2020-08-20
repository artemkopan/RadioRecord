package io.radio

import android.app.Application
import io.radio.data.player.AndroidPlayerServiceHolder
import io.radio.di.androidAppModule
import io.shared.di.commonModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RadioApp)
            modules(commonModules.plus(androidAppModule))
        }
        AndroidPlayerServiceHolder.initialize(this)
    }

}
