package io.shared.core

@Suppress("unused", "NOTHING_TO_INLINE")
object Logger {

    val printer = LoggerPrinter()

    inline fun d(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.DEBUG, message, throwable, tag)
    }

    inline fun v(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.VERBOSE, message, throwable, tag)
    }

    inline fun i(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.INFO, message, throwable, tag)
    }

    inline fun w(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.WARNING, message, throwable, tag)
    }

    inline fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.ERROR, message, throwable, tag)
    }

    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }
}

