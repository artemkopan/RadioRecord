package io.shared.core


expect class LoggerPrinter constructor() {
    inline fun performLog(
        priority: Logger.Level,
        message: String?,
        throwable: Throwable?,
        tag: String?
    )
}