package io.shared.core


expect class LoggerPrinter constructor() {
    fun performLog(
        priority: Logger.Level,
        tag: String?,
        throwable: Throwable?,
        message: String?
    )
}