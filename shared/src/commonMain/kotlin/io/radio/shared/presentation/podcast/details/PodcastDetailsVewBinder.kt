package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.mvi.Binder
import io.radio.shared.base.mvi.bind
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewBinder
import io.radio.shared.base.viewmodel.ViewBinderHelper
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.model.TrackItem
import io.radio.shared.presentation.podcast.details.PodcastDetailsView.*
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
) : ViewBinder(), Binder<PodcastDetailsView> {


    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)

    private val podcastStore = podcastDetailsStoreFactory.create(scope, stateStorage)
    private val playerStore = playerStoreFactory.create(scope, stateStorage)
    private val playlistStore: PlaylistStore = playlistStoreFactory.create(scope, stateStorage)

    init {
        combineTransform(
            podcastStore.stateFlow,
            playerStore.stateFlow,
            playlistStore.stateFlow,
            dispatchModelAndEffect()
        ).distinctUntilChanged().launchIn(scope)
    }

    override suspend fun bind(view: PodcastDetailsView) {
        bind {
            helper bindTo view

            view.intents.transform(intentToPlayerAction) bindTo playerStore
            view.intents.transform(intentToEffect) bindTo view

            podcastStore.stateFlow
                .distinctUntilChangedBy { it.tracks }
                .map(podcastStateToPlayerAction) bindTo playlistStore
        }
    }


    private fun dispatchModelAndEffect(): suspend FlowCollector<Model>.(PodcastDetailsStore.State, PlayerStore.State, PlaylistStore.State) -> Unit =
        { podcast, player, playlist ->
            helper.dispatchModel(
                Model(
                    podcast.podcastDetails?.cover.orEmpty(),
                    podcast.podcastDetails?.name.orEmpty(),
                    playlist.tracks
                )
            )

            if (podcast.error != null) {
                helper.dispatchEffect(Effect.PodcastError(errorFormatter.format(podcast.error)))
            }
            if (player.error != null) {
                helper.dispatchEffect(Effect.PlayerError(errorFormatter.format(player.error)))
            }
        }


    private val intentToEffect: suspend FlowCollector<Effect>.(Intent) -> Unit =
        {
            when (it) {
                is Intent.TrackClick -> emit(Effect.NavigateToPlayer)
            }
        }

    private val intentToPlayerAction: suspend FlowCollector<PlayerStore.Action>.(Intent) -> Unit =
        transform@{
            val switchPlayback: Boolean

            val trackItem: TrackItem
            when (it) {
                is Intent.TrackClick -> {
                    switchPlayback = false
                    trackItem = it.trackItem
                }
                is Intent.PlayPauseClick -> {
                    switchPlayback = true
                    trackItem = it.trackItem
                }
            }
            emit(
                PlayerStore.Action.Prepare(
                    track = trackItem,
                    tracks = playlistStore.stateFlow.first().playlist?.tracks ?: return@transform,
                    autoPlay = true,
                    forcePrepareIfTrackPrepared = false,
                    switchPlaybackIfTrackPrepared = switchPlayback
                )
            )
        }


    private val podcastStateToPlayerAction: suspend (value: PodcastDetailsStore.State) -> PlaylistStore.Action =
        {
            PlaylistStore.Action.ObserveTracksMediaState(it.tracks)
        }

}