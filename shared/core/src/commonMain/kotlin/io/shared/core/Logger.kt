package io.shared.core

@Suppress("unused")
object Logger {

    private val printer = LoggerPrinter()

    fun d(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.DEBUG, message, throwable, tag)
    }

    fun v(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.VERBOSE, message, throwable, tag)
    }

    fun i(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.INFO, message, throwable, tag)
    }

    fun w(message: String, throwable: Throwable? = null, tag: String? = null) {
        printer.performLog(Level.WARNING, message, throwable, tag)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String? = null) {
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

