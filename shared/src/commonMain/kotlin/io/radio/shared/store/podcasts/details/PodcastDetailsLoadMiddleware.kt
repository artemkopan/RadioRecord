package io.radio.shared.store.podcasts.details

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.mapper.TrackItemFromRadioPodcastMapper
import io.radio.shared.model.CoverImage
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*
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