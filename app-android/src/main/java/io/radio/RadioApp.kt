package io.radio

import android.app.Application
import io.radio.data.player.AndroidPlayerServiceHolder
import io.radio.di.androidAppModule
import io.shared.di.commonModules
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule

class RadioApp : Application(), DIAware {

    override fun onCreate() {
        super.onCreate()
        AndroidPlayerServiceHolder.initialize(this)
    }

    override val di: DI by DI.lazy {
        importAll(commonModules)
        importAll(androidAppModule, androidXModule(this@RadioApp))
    }

}
