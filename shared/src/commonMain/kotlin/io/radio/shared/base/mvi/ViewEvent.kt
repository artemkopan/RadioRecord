package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable

interface ViewEvent : Persistable {
    val tag: String
}