package io.shared.core

import android.util.Log


actual class LoggerPrinter {

    actual fun performLog(
        priority: Logger.Level,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        //todo add timber logging
        Log.d(tag, message, throwable)
    }
}