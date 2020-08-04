package io.radio.shared.formatters

import io.radio.shared.model.ResourceString

interface ErrorFormatter {

    fun format(throwable: Throwable): ResourceString

}