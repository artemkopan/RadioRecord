package io.shared.formatters

import io.shared.model.ResourceString

expect class ErrorFormatter {

    fun format(throwable: Throwable): ResourceString

}