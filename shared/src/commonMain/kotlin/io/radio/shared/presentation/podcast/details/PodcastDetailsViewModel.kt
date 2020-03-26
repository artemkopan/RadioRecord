package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Optional
import io.radio.shared.base.State
import io.radio.shared.base.isEmpty
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.base.viewmodel.ViewModelParams
import io.radio.shared.data.mapper.TrackItemFromRadioPodcastMapper
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.repositories.station.RadioRepository
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.model.TrackMediaInfo
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetailsViewModel constructor(
    private val repository: RadioRepository,
    private val trackMapper: TrackItemFromRadioPodcastMapper,
    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase,
    playerController: PlayerController,
    private val params: ViewModelParams
) : ViewModel() {


    private val podcastId get() = requireNotNull(params.get<PodcastDetailsParams>("params")).id

    private val podcastDetailsChannel = ConflatedBroadcastChannel<State<RadioPodcastDetails>>()
    val podcastDetailsFlow: Flow<State<RadioPodcastDetails>> get() = podcastDetailsChannel.asFlow()

    private val defaultTrackItemsChannel = ConflatedBroadcastChannel<List<TrackMediaInfo>>()

    private val trackItemsFlowInner = defaultTrackItemsChannel.asFlow()
        .combine(playerController.observeTrackInfo(), tracksCombiner())

    val trackItemsFlow: Flow<List<TrackMediaInfo>> get() = trackItemsFlowInner.debounce(150L)

    init {
        podcastDetailsChannel.perform {
            repository.getPodcast(podcastId).also {
                withContext(IoDispatcher) {
                    it.items
                        .map {
                            trackMediaInfoCreatorUseCase.execute(
                                TrackMediaInfoCreatorUseCase.Params(trackMapper.map(it))
                            )
                        }
                        .let { defaultTrackItemsChannel.send(it) }
                }
            }
        }
    }

    fun onPlayClick(mediaInfo: TrackMediaInfo) {
        scope.launch { trackMediaInfoProcessUseCase.execute(mediaInfo.track) }
    }

    fun onTrackClick(mediaInfo: TrackMediaInfo) {
        //todo open player
    }

    private fun tracksCombiner(): suspend (tracks: List<TrackMediaInfo>, modifyTrackOpt: Optional<TrackMediaInfo>) -> List<TrackMediaInfo> {
        return { tracks, modifyTrackOpt ->
            withContext(IoDispatcher) combiner@{
                if (modifyTrackOpt.isEmpty()) return@combiner tracks
                val modifyTrack = modifyTrackOpt.data!!
                val index = tracks.indexOfFirst { it.track.id == modifyTrack.track.id }
                if (index == -1) return@combiner tracks
                val mutableTracks = tracks.toMutableList()
                mutableTracks[index] = modifyTrack
                return@combiner mutableTracks
            }
        }
    }

}