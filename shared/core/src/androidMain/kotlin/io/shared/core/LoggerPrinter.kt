package io.shared.core

import android.util.Log
import timber.log.Timber


actual class LoggerPrinter {

    init {
        Timber.plant(Timber.DebugTree())
    }

    actual fun performLog(
        priority: Logger.Level,
        message: String?,
        throwable: Throwable?,
        tag: String?
    ) {
        val timberPriority = when(priority){
            Logger.Level.VERBOSE -> Log.VERBOSE
            Logger.Level.DEBUG -> Log.DEBUG
            Logger.Level.INFO -> Log.INFO
            Logger.Level.WARNING -> Log.WARN
            Logger.Level.ERROR -> Log.ERROR
        }
        Timber.tag(tag).log(timberPriority, throwable, message)
    }
}