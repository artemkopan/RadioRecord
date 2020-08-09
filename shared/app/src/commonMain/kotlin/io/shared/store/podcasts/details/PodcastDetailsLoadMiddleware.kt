package io.shared.store.podcasts.details

import io.shared.core.IoDispatcher
import io.shared.mapper.TrackItemFromRadioPodcastMapper
import io.shared.model.CoverImage
import io.shared.mvi.Middleware
import io.shared.repo.RadioRepository
import io.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transform

class PodcastDetailsLoadMiddleware(
    private val radioRepository: RadioRepository,
    private val trackItemFromRadioPodcastMapper: TrackItemFromRadioPodcastMapper
) : Middleware<Action, Result, State> {

    override fun accept(
        actionFlow: Flow<Action>,
        state: () -> State
    ): Flow<Result> {
        return actionFlow.transform {
            if (it is Action.LoadPodcastById) {
                emit(Result.Loading)
                val podcastDetails = radioRepository.getPodcast(it.id)
                emit(
                    Result.Podcast(
                        podcastDetails,
                        trackItemFromRadioPodcastMapper.mapList(
                            podcastDetails.items,
                            CoverImage(podcastDetails.cover)
                        )
                    )
                )
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.Error(cause)); true }
    }

}