package io.radio.shared.feature.radio.podcasts.details.middleware

import io.radio.shared.base.mvi.Middleware2
import io.radio.shared.base.mvi.middleware.Action
import io.radio.shared.base.mvi.middleware.Result
import io.radio.shared.mapper.TrackItemFromRadioPodcastMapper
import io.radio.shared.model.PodcastDetails
import io.radio.shared.model.TrackItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest

class GetPodcastTrackListMiddleware(
    private val trackItemFromRadioPodcastMapper: TrackItemFromRadioPodcastMapper
) : Middleware2<GetPodcastTrackListResult> {

    override fun dispatch(actionFlow: Flow<Action>): Flow<GetPodcastTrackListResult> {
        return actionFlow.transformLatest {
            if (it is GetPodcastTrackListAction) {
                emit(
                    GetPodcastTrackListResult(
                        trackItemFromRadioPodcastMapper.mapList(it.podcastDetails.items)
                    )
                )
            }
        }
    }

}

data class GetPodcastTrackListAction(val podcastDetails: PodcastDetails) : Action

data class GetPodcastTrackListResult(val trackList: List<TrackItem>) : Result