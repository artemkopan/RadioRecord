package io.shared.store.image

import io.shared.mvi.Reducer
import io.shared.mvi.StoreFactory
import io.shared.mvi.StoreImpl
import io.shared.mvi.StateStorage
import io.shared.store.image.ImageParamsStore.*
import kotlinx.coroutines.CoroutineScope

class ImageParamsStoreFactory(
    private val getImageParamsByUrlMiddleware: GetImageParamsByUrlMiddleware
) : StoreFactory<Action, Result, State> {

    override fun create(
        coroutineScope: CoroutineScope,
        stateStorage: StateStorage
    ): ImageParamsStore {
        return object : StoreImpl<Action, Result, State>(
            coroutineScope,
            listOf(getImageParamsByUrlMiddleware),
            emptyList(),
            ReducerImpl,
            State()
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