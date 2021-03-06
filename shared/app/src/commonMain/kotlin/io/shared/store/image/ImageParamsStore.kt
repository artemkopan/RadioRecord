package io.shared.store.image

import io.shared.core.Persistable
import io.shared.image.Color
import io.shared.image.ImageLightness
import io.shared.image.Size
import io.shared.mvi.Store
import io.shared.store.image.ImageParamsStore.*

interface ImageParamsStore : Store<Action, Result, State> {

    sealed class Action {

        data class GetImageParamsByUrl(
            val url: String, val size: Size, val defaultDominantColor: Color
        ) : Action()

    }

    sealed class Result {

        data class Error(val throwable: Throwable) : Result()

        data class ImageParams(
            val dominantColor: Color,
            val dominantDarkerColor: Color,
            val imageLightness: ImageLightness
        ) : Result()

    }

    data class State(
        val dominantColor: Color = Color.TRANSPARENT,
        val dominantDarkerColor: Color = Color.TRANSPARENT,
        val imageLightness: ImageLightness = ImageLightness.Unknown,
        val error: Throwable? = null
    ) : Persistable

}
