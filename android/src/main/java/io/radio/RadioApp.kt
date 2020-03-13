package io.radio

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import io.radio.di.DaggerRadioComponent
import io.radio.di.RadioComponent
import io.radio.shared.common.Logger
import timber.log.Timber

class RadioApp : Application() {

    val component: RadioComponent by lazy {
        DaggerRadioComponent.builder()
            .application(this)
            .context(this)
            .resources(resources)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Logger.addPrinter(object : Logger.Printer {
            override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
                Timber.tag(tag).log(priority, t, message)
            }
        })
    }

}

fun Fragment.di(func: RadioComponent.() -> Unit) {
    requireContext().di(func)
}

fun Context.di(func: RadioComponent.() -> Unit) {
    func((applicationContext as RadioApp).component)
}