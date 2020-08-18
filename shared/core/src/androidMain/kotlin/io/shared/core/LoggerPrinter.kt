package io.shared.core

import android.util.Log


actual class LoggerPrinter {
    actual fun performLog(
        priority: Logger.Level,
        message: String?,
        throwable: Throwable?,
        tag: String?
    ) {
        //todo add timber logging
        Log.d(tag, message, throwable)
    }
}