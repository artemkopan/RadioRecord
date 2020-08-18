package io.shared.core

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSThread

private const val CALL_STACK_INDEX = 8
private val defaultTag: String = "app"

actual class LoggerPrinter {

    var crashAssert = false

    private val dateFormatter = NSDateFormatter().apply {
        dateFormat = "MM-dd HH:mm:ss.SSS"
    }

    private val tagMap: HashMap<Logger.Level, String> = hashMapOf(
        Logger.Level.VERBOSE to "💜 VERBOSE",
        Logger.Level.DEBUG to "💚 DEBUG",
        Logger.Level.INFO to "💙 INFO",
        Logger.Level.WARNING to "💛 WARN",
        Logger.Level.ERROR to "❤️ ERROR"
    )

    actual fun performLog(
        priority: Logger.Level,
        message: String?,
        throwable: Throwable?,
        tag: String?
    ) {
        println(buildLog(priority, tag, message))
    }

    fun setTag(level: Logger.Level, tag: String) {
        tagMap[level] = tag
    }

    fun setDateFormatterString(formatter: String) {
        dateFormatter.dateFormat = formatter
    }

    private fun getCurrentTime() = dateFormatter.stringFromDate(NSDate())

    private fun buildLog(priority: Logger.Level, tag: String?, message: String?): String {
        return "${getCurrentTime()} ${tagMap[priority]} ${tag ?: performTag(defaultTag)} - $message"
    }

    // find stack trace
    private fun performTag(tag: String): String {
        val thread = NSThread.callStackSymbols

        return if (thread.size >= CALL_STACK_INDEX) {
            createStackElementTag(thread[CALL_STACK_INDEX] as String)
        } else {
            tag
        }
    }

    internal fun createStackElementTag(string: String): String {
        var tag = string
        tag = tag.substringBeforeLast('$')
        tag = tag.substringBeforeLast('(')
        tag = tag.substring(tag.lastIndexOf(".", tag.lastIndexOf(".") - 1) + 1)
        tag = tag.replace("$", "")
        tag = tag.replace("COROUTINE", "")
        return tag
    }

}