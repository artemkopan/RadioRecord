package io.shared.store.image

import io.shared.mvi.*
import io.shared.store.image.ImageParamsStore.*
import kotlinx.coroutines.CoroutineScope

class ImageParamsStoreFactory(
    private val getImageParamsByUrlMiddleware: GetImageParamsByUrlMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        tag: String,
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): ImageParamsStore {
        return object : StoreImpl<Action, Result, State>(
            tag,
            coroutineScope,
            listOf(getImageParamsByUrlMiddleware),
            emptyList(),
            ReducerImpl,
            stateStorage.getOrDefault("image-params-store_$tag") { State() }
        ), ImageParamsStore {}
    }

    private object ReducerImpl : Reducer<Result, State> {
        override fun reduce(result: Result, state: State): State = with(result) {
            return when (this) {
                is Result.Error -> state.copy(error = throwable)
                is Result.ImageParams -> state.copy(
                    dominantColor = dominantColor,
                    dominantDarkerColor = dominantDarkerColor,
                    imageLightness = imageLightness,
                    error = null
                )
            }
        }
    }
}