package io.shared.presentation.podcast.details

import io.shared.core.Logger
import io.shared.formatters.ErrorFormatter
import io.shared.model.TrackItem
import io.shared.mvi.Binder
import io.shared.mvi.StateStorage
import io.shared.mvi.ViewBinder
import io.shared.mvi.ViewBinderHelper
import io.shared.presentation.podcast.details.PodcastDetailsView.*
import io.shared.store.player.PlayerStore
import io.shared.store.player.PlayerStoreFactory
import io.shared.store.playlist.PlaylistStore
import io.shared.store.playlist.PlaylistStoreFactory
import io.shared.store.podcasts.details.PodcastDetailsStore
import io.shared.store.podcasts.details.PodcastDetailsStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class PodcastDetailsViewBinder(
    stateStorage: StateStorage,
    podcastDetailsStoreFactory: PodcastDetailsStoreFactory,
    playerStoreFactory: PlayerStoreFactory,
    playlistStoreFactory: PlaylistStoreFactory,
    private val errorFormatter: ErrorFormatter
) : ViewBinder(), Binder<Intent, Model, Effect> {


    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)

    private val podcastStore =
        podcastDetailsStoreFactory.create("podcast-details", scope, stateStorage)
    private val playerStore = playerStoreFactory.create("podcast-details", scope, stateStorage)
    private val playlistStore: PlaylistStore =
        playlistStoreFactory.create("podcast-details", scope, stateStorage)

    init {
        combineTransform(
            podcastStore.stateFlow,
            playerStore.stateFlow,
            playlistStore.stateFlow,
            dispatchModelAndEffect()
        )
            .distinctUntilChanged()
            .launchIn(scope)

        podcastStore.stateFlow
            .distinctUntilChangedBy { it.tracks }
            .map(podcastStateToPlayerAction())
            .bindTo(playlistStore, scope)
    }

    override fun bindIntents(
        scope: CoroutineScope,
        intentFlow: Flow<Intent>
    ) {
        intentFlow.transform(intentToPlayerAction).bindTo(playerStore, scope)
        Logger.d("intent effect", tag = "TEST")
        intentFlow.transform(intentToEffect).onEach { helper.dispatchEffect(it) }.launchIn(scope)
    }

    override val modelFlow: Flow<Model>
        get() = helper.modelFlow

    override val effectFlow: Flow<Effect>
        get() = helper.effectFlow

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
            if (it is Intent.TrackClick) {
                emit(Effect.NavigateToPlayer)
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


    private fun podcastStateToPlayerAction(): suspend (value: PodcastDetailsStore.State) -> PlaylistStore.Action =
        {
            PlaylistStore.Action.ObserveTracksMediaState(it.tracks)
        }

}