package io.radio

import android.app.Application
import io.radio.data.player.AndroidPlayerServiceHolder
import io.radio.di.appModules
import io.radio.shared.base.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RadioApp)
            modules(*appModules)
        }

        Timber.plant(Timber.DebugTree())
        Logger.addPrinter(object : Logger.Printer {
            override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
                Timber.tag(tag).log(priority, t, message)
            }
        })

        AndroidPlayerServiceHolder.initialize(this)
    }

}
