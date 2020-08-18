package io.shared.core


expect class LoggerPrinter constructor() {
    fun performLog(
        priority: Logger.Level,
        message: String?,
        throwable: Throwable?,
        tag: String?
    )
}