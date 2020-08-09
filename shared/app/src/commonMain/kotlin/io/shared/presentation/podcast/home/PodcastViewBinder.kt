package io.shared.presentation.podcast.home

import io.shared.formatters.ErrorFormatter
import io.shared.image.Color
import io.shared.image.ImageLightness
import io.shared.image.Size
import io.shared.model.Podcast
import io.shared.mvi.*
import io.shared.presentation.podcast.details.PodcastDetailsParams
import io.shared.presentation.podcast.home.PodcastView.*
import io.shared.resources.AppResources
import io.shared.store.image.ImageParamsStore
import io.shared.store.image.ImageParamsStoreFactory
import io.shared.store.podcasts.home.PodcastStore
import io.shared.store.podcasts.home.PodcastStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class PodcastViewBinder(
    private val stateStorage: StateStorage,
    podcastStoreFactory: PodcastStoreFactory,
    imageParamsStoreFactory: ImageParamsStoreFactory,
    private val errorFormatter: ErrorFormatter,
    private val appResources: AppResources
) : ViewBinder(), Binder<PodcastView> {

    private val helper = ViewBinderHelper<Model, Effect>(stateStorage)

    private val podcastStore = podcastStoreFactory.create(scope, stateStorage)
    private val imageStore = imageParamsStoreFactory.create(scope, stateStorage)

    init {
        podcastStore.stateFlow.onEach {
            it.dispatchModel()
            it.dispatchEffect()
        }.launchIn(scope)

        imageStore.stateFlow.onEach {
            it.dispatchEffectFromImageParamsStore()
        }.launchIn(scope)
    }

    override suspend fun bind(view: PodcastView) {
        bind {
            helper bindTo view
            view.intents.mapIntentToImageParamsAction() bindTo imageStore
        }
    }

    private fun Flow<Intent>.mapIntentToImageParamsAction() = mapNotNull {
        if (it is Intent.SelectPodcast) {
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

    private suspend fun PodcastStore.State.dispatchModel() {
        helper.dispatchModel(Model(isLoading, podcastList))
    }

    private suspend fun PodcastStore.State.dispatchEffect() {
        error?.let { throwable ->
            Effect.Error(
                errorFormatter.format(throwable)
            )
        }?.let {
            helper.dispatchEffect(it)
        }
    }

    private suspend fun ImageParamsStore.State.dispatchEffectFromImageParamsStore() {
        val podcast = stateStorage.get<Podcast>(KEY_PODCAST) ?: return
        when {
            error != null -> Effect.Error(
                errorFormatter.format(error)
            )
            else -> {
                Effect.NavigateToDetails(
                    PodcastDetailsParams(
                        id = podcast.id,
                        name = podcast.name,
                        cover = podcast.cover,
                        headerColor = dominantDarkerColor.value,
                        toolbarColor = if (imageLightness == ImageLightness.Dark) {
                            appResources.primaryColor
                        } else {
                            appResources.accentColor
                        }
                    )
                )
            }
        }.let { helper.dispatchEffect(it) }
    }

    companion object {
        private const val KEY_PODCAST = "podcast"
    }

}