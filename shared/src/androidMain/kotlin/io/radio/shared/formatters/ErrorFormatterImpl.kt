package io.radio.shared.formatters

import io.radio.shared.model.ResourceString

class ErrorFormatterImpl : ErrorFormatter {

    override fun format(throwable: Throwable): ResourceString {
        return ResourceString(throwable.message.orEmpty())
        //todo add implementation for different exceptions
    }
}