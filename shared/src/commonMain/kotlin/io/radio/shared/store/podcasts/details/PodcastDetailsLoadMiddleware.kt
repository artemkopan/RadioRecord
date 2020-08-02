package io.radio.shared.store.podcasts.details

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.mvi.Middleware
import io.radio.shared.mapper.TrackItemFromRadioPodcastMapper
import io.radio.shared.repo.RadioRepository
import io.radio.shared.store.podcasts.details.PodcastDetailsStore.*
import kotlinx.coroutines.flow.*

class PodcastDetailsLoadMiddleware(
    private val radioRepository: RadioRepository,
    private val trackItemFromRadioPodcastMapper: TrackItemFromRadioPodcastMapper
) : Middleware<Action, Result, State> {

    override fun accept(
        actions: Flow<Action>,
        state: StateFlow<State>
    ): Flow<Result> {
        return actions.transform {
            if (it is Action.LoadPodcastById) {
                emit(Result.Loading)
                val podcastDetails = radioRepository.getPodcast(it.id)
                emit(
                    Result.Podcast(
                        podcastDetails,
                        trackItemFromRadioPodcastMapper.mapList(podcastDetails.items)
                    )
                )
            }
        }
            .flowOn(IoDispatcher)
            .retryWhen { cause, _ -> emit(Result.Error(cause)); true }
    }

}