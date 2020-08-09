package io.shared.mvi

interface ViewEffects<in Effect : Any> {

    fun acceptEffect(effect: Effect)

}