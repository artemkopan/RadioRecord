package io.radio.shared.base.mvi

interface ViewEvents<in Event : Any> {

    fun acceptEvent(event: Event)

}