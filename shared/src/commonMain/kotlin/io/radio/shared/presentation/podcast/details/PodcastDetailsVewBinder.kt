package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.extensions.formatTag
import io.radio.shared.base.mvi.bind
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewBinder
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.model.TrackItem
import io.radio.shared.presentation.podcast.details.PodcastDetailsView.Model
import io.radio.shared.store.player.PlayerStore
import io.radio.shared.store.player.PlayerStoreFactory
import io.radio.shared.store.playlist.PlaylistStore
import io.radio.shared.store.playlist.PlaylistStoreFactory
import io.radio.shared.store.podcasts.details.PodcastDetailsStore
import io.radio.shared.store.podcasts.details.PodcastDetailsStoreFactory
import kotlinx.coroutines.flow.*

class PodcastDetailsVewBinder(
    stateStorage: StateStorage,
    podcastDetailsStoreFactory: PodcastDetailsStoreFactory,
    playerStoreFactory: PlayerStoreFactory,
    playlistStoreFactory: PlaylistStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder() {

    private val podcastStore = podcastDetailsStoreFactory.create(scope, stateStorage)
    private val playerStore = playerStoreFactory.create(scope, stateStorage)
    private val playlistStore: PlaylistStore = playlistStoreFactory.create(scope, stateStorage)

    suspend fun attachView(view: PodcastDetailsView) {
        bind {
            combineTransform(
                podcastStore.stateFlow,
                playerStore.stateFlow,
                playlistStore.stateFlow,
                stateToModel
            ).distinctUntilChanged() bindTo view

            combineTransform(
                podcastStore.stateFlow,
                playerStore.stateFlow,
                stateToEvent
            ) bindTo view

            view.intents.transform(intentToPlayerAction) bindTo playerStore
            view.intents.transform(intentToEvent) bindTo view

            podcastStore.stateFlow
                .distinctUntilChangedBy { it.tracks }
                .map(podcastStateToPlayerAction) bindTo playlistStore
        }
    }


    private val stateToModel: suspend FlowCollector<Model>.(PodcastDetailsStore.State, PlayerStore.State, PlaylistStore.State) -> Unit =
        { podcast, player, playlist ->
            emit(
                Model(
                    podcast.podcastDetails?.cover.orEmpty(),
                    podcast.podcastDetails?.name.orEmpty(),
                    playlist.tracks
                )
            )
        }

    private val stateToEvent: suspend FlowCollector<PodcastDetailsView.Event>.(a: PodcastDetailsStore.State, b: PlayerStore.State) -> Unit =
        { podcast, _ ->
            podcast.error?.let {
                emit(
                    PodcastDetailsView.Event.Error(
                        errorFormatter.format(it),
                        it formatTag "podcast_details"
                    )
                )
            }
        }

    private val intentToEvent: suspend FlowCollector<PodcastDetailsView.Event>.(PodcastDetailsView.Intent) -> Unit =
        {
            when (it) {
                is PodcastDetailsView.Intent.TrackClick -> emit(PodcastDetailsView.Event.NavigateToPlayer)
            }
        }

    private val intentToPlayerAction: suspend FlowCollector<PlayerStore.Action>.(PodcastDetailsView.Intent) -> Unit =
        transform@{
            val trackItem: TrackItem = when (it) {
                is PodcastDetailsView.Intent.TrackClick -> it.trackItem
                is PodcastDetailsView.Intent.PlayPauseClick -> it.trackItem
            }
            emit(
                PlayerStore.Action.PrepareOrSwitchPlayPause(
                    track = trackItem,
                    tracks = playlistStore.stateFlow.first().playlist?.tracks ?: return@transform,
                    autoPlay = true
                )
            )
        }


    private val podcastStateToPlayerAction: suspend (value: PodcastDetailsStore.State) -> PlaylistStore.Action =
        {
            PlaylistStore.Action.ObserveTracksMediaState(it.tracks)
        }

}