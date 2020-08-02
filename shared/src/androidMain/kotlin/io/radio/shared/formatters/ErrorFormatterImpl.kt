package io.radio.shared.formatters

class ErrorFormatterImpl : ErrorFormatter {

    override fun format(throwable: Throwable): String {
        return throwable.message.orEmpty()
        //todo add implementation for different exceptions
    }
}