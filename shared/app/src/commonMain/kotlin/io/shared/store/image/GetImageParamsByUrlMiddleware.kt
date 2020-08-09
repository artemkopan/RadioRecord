package io.shared.store.image

import io.shared.core.IoDispatcher
import io.shared.image.ImageProcessor
import io.shared.mvi.Middleware
import io.shared.store.image.ImageParamsStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform

class GetImageParamsByUrlMiddleware(
    private val imageProcessor: ImageProcessor
) : Middleware<Action, Result, State> {

    override fun accept(actionFlow: Flow<Action>, state: () -> State): Flow<Result> {
        return actionFlow.transform {
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