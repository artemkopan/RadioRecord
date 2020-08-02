package io.radio.shared.store.image

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.image.ImageProcessor
import io.radio.shared.store.image.ImageParamsStore.*
import kotlinx.coroutines.flow.*

class GetImageParamsByUrlMiddleware(
    private val imageProcessor: ImageProcessor
) : Middleware<Action, Result, State> {

    override fun accept(actions: Flow<Action>, state: StateFlow<State>): Flow<Result> {
        return actions.transform {
            if (it is Action.GetImageParamsByUrl) {
                val image = imageProcessor.getImage(it.url, it.size.width, it.size.height)
                val palette = imageProcessor.generatePalette(image)
                val dominantColor = imageProcessor.getDominantColor(
                    palette,
                    it.defaultDominantColor
                )

                emit(
                    Result.ImageParams(
                        dominantColor,
                        imageProcessor.getDarkerColor(dominantColor),
                        imageProcessor.getLightness(palette)
                    ) as Result
                )
            }

        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ ->
                emit(Result.Error(cause))
                true
            }
    }
}