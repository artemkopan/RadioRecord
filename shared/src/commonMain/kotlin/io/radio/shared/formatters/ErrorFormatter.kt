package io.radio.shared.formatters

interface ErrorFormatter {

    fun format(throwable: Throwable): String

}