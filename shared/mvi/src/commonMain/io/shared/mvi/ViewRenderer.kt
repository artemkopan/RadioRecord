package io.radio.shared.base.mvi


/**
 * Represents a consumer of the `View Models`
 *
 * @see MviView
 */
interface ViewRenderer<in Model : Any> {

    /**
     * Renders (displays) the provided `View Model`
     *
     * @param model a `View Model` to be rendered (displayed)
     */
    fun render(model: Model)

}
