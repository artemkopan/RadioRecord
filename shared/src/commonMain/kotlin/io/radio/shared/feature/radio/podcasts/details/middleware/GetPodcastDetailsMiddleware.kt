package io.radio.shared.feature.radio.podcasts.details.middleware

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.feature.radio.RadioRepository
import io.radio.shared.model.PodcastDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform

class GetPodcastDetailsMiddleware(
    private val radioRepository: RadioRepository,
    private val podcastId: Int
) : Middleware2<GetPodcastDetailsResult> {

    override fun dispatch(actionFlow: Flow<Action>): Flow<GetPodcastDetailsResult> {
        return actionFlow.transform {
            if (it is GetPodcastDetailsAction) {
                emit(GetPodcastDetailsResult.Loading)
                emit(GetPodcastDetailsResult.Success(radioRepository.getPodcast(podcastId)))
            }
        }
            .flowOn(IoDispatcher)
            .catch { emit(GetPodcastDetailsResult.Error(it)) }
    }
}

object GetPodcastDetailsAction : Action

sealed class GetPodcastDetailsResult : Result {
    object Loading : GetPodcastDetailsResult()
    data class Success(val podcastDetails: PodcastDetails) : GetPodcastDetailsResult()
    data class Error(val throwable: Throwable) : GetPodcastDetailsResult()
}
