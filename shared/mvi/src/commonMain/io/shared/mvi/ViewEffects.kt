package io.radio.shared.base.mvi

interface ViewEffects<in Effect : Any> {

    fun acceptEffect(effect: Effect)

}