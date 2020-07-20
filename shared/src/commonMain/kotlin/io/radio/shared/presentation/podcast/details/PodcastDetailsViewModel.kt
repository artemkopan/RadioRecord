package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.*
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.data.mapper.TrackItemFromRadioPodcastMapper
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.repositories.station.RadioRepository
import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessParams
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.model.CoverImage
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetailsViewModel constructor(
    private val repository: RadioRepository,
    private val trackMapper: TrackItemFromRadioPodcastMapper,
    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase,
    playerController: PlayerController,
    private val params: StateStorage
) : ViewModel() {

    private val podcastId get() = requireNotNull(params.get<PodcastDetailsParams>("params")).id

    private val podcastDetailsChannel = ConflatedBroadcastChannel<State<RadioPodcastDetails>>()
    val podcastDetailsFlow: Flow<State<RadioPodcastDetails>> get() = podcastDetailsChannel.asFlow()

    private val openPlayerEventChannel = ConflatedBroadcastChannel<Event<Unit>>()
    val openPlayerEventFlow: Flow<Event<Unit>> get() = openPlayerEventChannel.asFlow()

    private val defaultTrackItemsChannel = ConflatedBroadcastChannel<List<TrackMediaInfo>>()

    private val trackItemsChannel = ConflatedBroadcastChannel<List<TrackMediaInfo>>()
    val trackItemsFlow: Flow<List<TrackMediaInfo>> get() = trackItemsChannel.asFlow()

    val trackPositionFlow = playerController.observePlaylist().mapNotNull {
        it.data?.position?.takeIf { pos -> pos >= 0 }
    }

    private var playlist = emptyList<TrackItem>()

    init {
        defaultTrackItemsChannel.asFlow()
            .combine(playerController.observeTrackInfo(), tracksCombiner())
            .onEach { trackItemsChannel.send(it) }
            .catch { /* TODO add handling exception */ }
            .launchIn(scope)

        podcastDetailsChannel.perform {
            repository.getPodcast(podcastId).also { details ->
                withContext(IoDispatcher) {
                    val playlistMutable = mutableListOf<TrackItem>()
                    details.items
                        .map {
                            val track = trackMapper.map(it, CoverImage(details.cover))
                            playlistMutable.add(track)
                            trackMediaInfoCreatorUseCase.execute(
                                TrackMediaInfoCreatorUseCase.Params(track)
                            )
                        }
                        .let {
                            playlist = playlistMutable
                            defaultTrackItemsChannel.send(it)
                        }
                }
            }
        }
    }

    fun onPlayClick(mediaInfo: TrackMediaInfo) {
        scope.launch {
            trackMediaInfoProcessUseCase.execute(
                TrackMediaInfoProcessParams(mediaInfo.track, playlist)
            )
        }
    }

    fun onTrackClick(mediaInfo: TrackMediaInfo) {
        scope.launch {
            trackMediaInfoProcessUseCase.execute(
                TrackMediaInfoProcessParams(mediaInfo.track, playlist, justPrepare = true)
            )
            openPlayerEventChannel.send(Event(Unit))
        }
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