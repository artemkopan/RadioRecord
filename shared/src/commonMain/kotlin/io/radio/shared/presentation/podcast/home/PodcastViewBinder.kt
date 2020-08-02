package io.radio.shared.presentation.podcast.home

import io.radio.shared.base.extensions.formatTag
import io.radio.shared.base.mvi.bind
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewBinder
import io.radio.shared.formatters.ErrorFormatter
import io.radio.shared.image.Color
import io.radio.shared.image.ImageLightness
import io.radio.shared.image.Size
import io.radio.shared.model.Podcast
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.resources.AppResources
import io.radio.shared.store.image.ImageParamsStore
import io.radio.shared.store.image.ImageParamsStoreFactory
import io.radio.shared.store.podcasts.home.PodcastStore
import io.radio.shared.store.podcasts.home.PodcastStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class PodcastViewBinder(
    private val stateStorage: StateStorage,
    podcastStoreFactory: PodcastStoreFactory,
    imageParamsStoreFactory: ImageParamsStoreFactory,
    private val errorFormatter: ErrorFormatter,
    private val appResources: AppResources
) : ViewBinder() {

    private val podcastStore = podcastStoreFactory.create(scope, stateStorage)
    private val imageStore = imageParamsStoreFactory.create(scope, stateStorage)

    suspend fun attachView(view: PodcastView) {
        bind {
            podcastStore.stateFlow.mapPodcastStateToModel() bindTo view
            podcastStore.stateFlow.mapPodcastStateToEvent() bindTo view

            view.intents.mapIntentToImageParamsAction() bindTo imageStore
            imageStore.stateFlow.mapImageParamsStateToEvent() bindTo view
        }
    }

    private fun Flow<PodcastView.Intent>.mapIntentToImageParamsAction() = mapNotNull {
        if (it is PodcastView.Intent.SelectPodcast) {
            stateStorage[KEY_PODCAST] = it.podcast
            ImageParamsStore.Action.GetImageParamsByUrl(
                it.podcast.cover,
                Size(width = 100, height = 100),
                Color.TRANSPARENT
            )
        } else {
            null
        }
    }

    private fun Flow<PodcastStore.State>.mapPodcastStateToModel() = map {
        PodcastView.Model(it.isLoading, it.data)
    }

    private fun Flow<PodcastStore.State>.mapPodcastStateToEvent() = mapNotNull {
        it.error?.let { throwable ->
            PodcastView.Event.Error(
                errorFormatter.format(throwable),
                throwable formatTag "loading_podcast"
            )
        }
    }

    private fun Flow<ImageParamsStore.State>.mapImageParamsStateToEvent() = mapNotNull {
        val podcast = stateStorage.get<Podcast>(KEY_PODCAST) ?: return@mapNotNull null
        return@mapNotNull when {
            it.error != null -> PodcastView.Event.Error(
                errorFormatter.format(it.error),
                it.error formatTag "loading_details_${podcast.id}"
            )
            else -> {
                PodcastView.Event.NavigateToDetails(
                    PodcastDetailsParams(
                        id = podcast.id,
                        name = podcast.name,
                        cover = podcast.cover,
                        headerColor = it.dominantDarkerColor.value,
                        toolbarColor = if (it.imageLightness == ImageLightness.Dark) {
                            appResources.primaryColor
                        } else {
                            appResources.accentColor
                        }
                    )
                )
            }
        }
    }

    companion object {
        private const val KEY_PODCAST = "podcast"
    }

}