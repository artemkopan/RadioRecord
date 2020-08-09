package io.shared.formatters

import io.shared.model.ResourceString

actual class ErrorFormatter {
    actual fun format(throwable: Throwable): ResourceString {
        return ResourceString(throwable.message.orEmpty())
        //todo add implementation for different exceptions
    }

}