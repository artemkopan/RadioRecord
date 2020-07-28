//package io.radio.shared.presentation.podcast.details
//
//import io.radio.shared.base.*
//import io.radio.shared.base.viewmodel.StateStorage
//import io.radio.shared.base.viewmodel.ViewModel
//import io.radio.shared.mapper.TrackItemFromRadioPodcastMapper
//import io.radio.shared.domain.player.PlayerController
//import io.radio.shared.feature.radio.RadioRepository
//import io.radio.shared.domain.usecases.track.TrackMediaInfoCreatorUseCase
//import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessParams
//import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
//import io.radio.shared.feature.player.MediaPlayer
//import io.radio.shared.model.*
//import kotlinx.coroutines.channels.ConflatedBroadcastChannel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class PodcastDetailsViewModelOld constructor(
//    private val params: StateStorage,
//    private val repository: RadioRepository,
//    private val trackMapper: TrackItemFromRadioPodcastMapper,
//    private val trackMediaInfoCreatorUseCase: TrackMediaInfoCreatorUseCase,
//    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase,
//    playerController: PlayerController,
//    private val mediaPlayer: MediaPlayer
//) : ViewModel() {
//
//    private val podcastId get() = requireNotNull(params.get<PodcastDetailsParams>("params")).id
//
//    private val podcastDetailsChannel = ConflatedBroadcastChannel<State<PodcastDetails>>()
//    val podcastDetailsFlow: Flow<State<PodcastDetails>> get() = podcastDetailsChannel.asFlow()
//
//    private val openPlayerEventChannel = ConflatedBroadcastChannel<Event<Unit>>()
//    val openPlayerEventFlow: Flow<Event<Unit>> get() = openPlayerEventChannel.asFlow()
//
//    private val defaultTrackItemsChannel = ConflatedBroadcastChannel<List<TrackItemWithMediaState>>()
//
//    private val trackItemsChannel = ConflatedBroadcastChannel<List<TrackItemWithMediaState>>()
//    val trackItemsFlow: Flow<List<TrackItemWithMediaState>> get() = trackItemsChannel.asFlow()
//
//    val trackPositionFlow = playerController.observePlaylist().mapNotNull {
//        it.data?.position?.takeIf { pos -> pos >= 0 }
//    }
//
//    private var playlist = emptyList<TrackItem>()
//
//    init {
//        defaultTrackItemsChannel.asFlow()
//            .combine(playerController.observeTrackInfo(), tracksCombiner())
//            .onEach { trackItemsChannel.send(it) }
//            .catch { /* TODO add handling exception */ }
//            .launchIn(scope)
//
//        podcastDetailsChannel.perform {
//            repository.getPodcast(podcastId).also { details ->
//                withContext(IoDispatcher) {
//                    val playlistMutable = mutableListOf<TrackItem>()
//                    details.items
//                        .map {
//                            val track = trackMapper.map(it, CoverImage(details.cover))
//                            playlistMutable.add(track)
//                            trackMediaInfoCreatorUseCase.execute(
//                                TrackMediaInfoCreatorUseCase.Params(track)
//                            )
//                        }
//                        .let {
//                            playlist = playlistMutable
//                            defaultTrackItemsChannel.send(it)
//                        }
//                }
//            }
//        }
//    }
//
//    fun onPlayClick(mediaInfo: TrackItemWithMediaState) {
//        scope.launch {
//            trackMediaInfoProcessUseCase.execute(
//                TrackMediaInfoProcessParams(mediaInfo.track, playlist)
//            )
//            mediaPlayer.prepare(
//                mediaInfo.track,
//                Playlist(playlist, playlist.indexOf(mediaInfo.track)),
//                true
//            )
//        }
//    }
//
//    fun onTrackClick(mediaInfo: TrackItemWithMediaState) {
//        scope.launch {
//            trackMediaInfoProcessUseCase.execute(
//                TrackMediaInfoProcessParams(mediaInfo.track, playlist, justPrepare = true)
//            )
//            openPlayerEventChannel.send(Event(Unit))
//        }
//    }
//
//    private fun tracksCombiner(): suspend (tracks: List<TrackItemWithMediaState>, modifyTrackOpt: Optional<TrackItemWithMediaState>) -> List<TrackItemWithMediaState> {
//        return { tracks, modifyTrackOpt ->
//            withContext(IoDispatcher) combiner@{
//                if (modifyTrackOpt.isEmpty()) return@combiner tracks
//                val modifyTrack = modifyTrackOpt.data!!
//                val index = tracks.indexOfFirst { it.track.id == modifyTrack.track.id }
//                if (index == -1) return@combiner tracks
//                val mutableTracks = tracks.toMutableList()
//                mutableTracks[index] = modifyTrack
//                return@combiner mutableTracks
//            }
//        }
//    }
//
//}