package io.radio.shared.common

import androidx.annotation.IntDef


@Suppress("unused")
object Logger {

    private const val TAG = "Logger"

    /**
     * Priority constant for the println method;
     */
    const val DEBUG = 3

    /**
     * Priority constant for the println method;
     */
    const val INFO = 4

    /**
     * Priority constant for the println method;
     */
    const val WARN = 5

    /**
     * Priority constant for the println method;
     */
    const val ERROR = 6

    private val PRINTERS = ArrayList<Printer>()

    private val tag: String
        get() {
            var tag = TAG

            if (PRINTERS.size == 0) return tag

            val stackTrace = Thread.currentThread().stackTrace

            if (stackTrace.size > 3) {
                val stackTraceElement = stackTrace[5]
                var fileName: String? = stackTraceElement.fileName
                if (fileName == null) fileName = ""
                tag = "(" + fileName + ":" + stackTraceElement.lineNumber + ") " + stackTraceElement.methodName
            }

            return tag
        }

    fun addPrinter(printer: Printer) {
        PRINTERS.add(printer)
    }

    fun removePrinter(printer: Printer) {
        PRINTERS.remove(printer)
    }

    @JvmOverloads
    @JvmStatic
    fun d(message: String, t: Throwable? = null) {
        d(
            tag,
            message,
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun d(tag: String, message: String, t: Throwable? = null) {
        for (printer in PRINTERS) {
            printer.log(DEBUG, tag, message, t)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun i(message: String, t: Throwable? = null) {
        i(
            tag,
            message,
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun i(tag: String, message: String, t: Throwable? = null) {
        for (printer in PRINTERS) {
            printer.log(INFO, tag, message, t)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun w(t: Throwable? = null) {
        w(
            tag,
            "",
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun w(message: String, t: Throwable? = null) {
        w(
            tag,
            message,
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun w(tag: String, message: String, t: Throwable? = null) {
        for (printer in PRINTERS) {
            printer.log(WARN, tag, message, t)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun e(t: Throwable? = null) {
        e(
            tag,
            "",
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun e(message: String, t: Throwable? = null) {
        e(
            tag,
            message,
            t
        )
    }

    @JvmOverloads
    @JvmStatic
    fun e(tag: String, message: String, t: Throwable? = null) {
        for (printer in PRINTERS) {
            printer.log(ERROR, tag, message, t)
        }
    }

    interface Printer {

        fun log(@Priority priority: Int, tag: String, message: String, t: Throwable?)

    }

    @IntDef(value = [DEBUG, INFO, WARN, ERROR])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Priority
}