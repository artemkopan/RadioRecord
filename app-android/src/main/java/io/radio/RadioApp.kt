package io.radio

import android.app.Application
import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier
import io.radio.data.player.AndroidPlayerServiceHolder
import io.radio.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RadioApp)
            modules(*appModules)
        }

        Napier.base(DebugAntilog())
        AndroidPlayerServiceHolder.initialize(this)
    }

}
