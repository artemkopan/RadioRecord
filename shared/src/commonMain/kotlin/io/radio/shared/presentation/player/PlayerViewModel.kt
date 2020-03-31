package io.radio.shared.presentation.player

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Optional
import io.radio.shared.base.toOptional
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.domain.image.ImageProcessor
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.player.StreamMetaData
import io.radio.shared.domain.resources.AppResources
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.domain.usecases.track.TrackUpdatePositionUseCase
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaState
import io.radio.shared.model.TrackMediaTimeLine
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    playerController: PlayerController,
    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase,
    private val trackUpdatePositionUseCase: TrackUpdatePositionUseCase,
    appResources: AppResources,
    imageProcessor: ImageProcessor
) : ViewModel() {


    val trackFlow: Flow<TrackItem> =
        playerController.observeTrackInfo().mapNotNull { it.data?.track }.distinctUntilChanged()

    val trackMediaStateFlow: Flow<Optional<TrackMediaState>> =
        playerController.observeTrackInfo().map { it.data?.state.toOptional() }

    val subTitleFlow = playerController.observeStreamMetaData()
        .combine(trackFlow, subTitleCombiner())

    private val scrubbingTimeFormattedChannel = BroadcastChannel<String>(1)
    val scrubbingTimeFormattedFlow: Flow<String> get() = scrubbingTimeFormattedChannel.asFlow()

    val trackTimeLineFlow: Flow<Optional<TrackMediaTimeLine>> =
        playerController.observeTrackTimeLine()

    private val visualizationColorChannel = ConflatedBroadcastChannel(appResources.accentColor)
    val visualizationColorFlow: Flow<Int> get() = visualizationColorChannel.asFlow()

    init {
        trackFlow.onEach {
            withContext(IoDispatcher) {
                val defaultColor = visualizationColorChannel.value
                val visualizationColor = it.cover.data?.img?.takeIf { it.isNotEmpty() }?.let {
                    imageProcessor.getDominantColor(it, defaultColor)
                } ?: defaultColor
                visualizationColorChannel.send(visualizationColor)
            }
        }
            .catch { /* todo add handling exception */ }
            .launchIn(scope)
    }

    fun onPlayClicked() {
        scope.launch {
            trackMediaInfoProcessUseCase.execute(trackFlow.first())
        }
    }

    fun onPositionChanged(position: Int, isScrubbing: Boolean) {
        scope.launch {
            scrubbingTimeFormattedChannel.send(
                trackUpdatePositionUseCase.execute(
                    position,
                    isScrubbing
                )
            )
        }
    }

    private fun subTitleCombiner(): suspend (streamOpt: Optional<StreamMetaData>, track: TrackItem) -> String =
        { streamOpt, track ->
            if (track.source.isStream) {
                streamOpt.data?.title.orEmpty()
            } else {
                track.subTitle
            }
        }

}