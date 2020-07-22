package io.radio.shared.presentation.player

import io.radio.shared.base.Optional
import io.radio.shared.base.extensions.CoroutineExceptionHandler
import io.radio.shared.base.extensions.JobRunner
import io.radio.shared.base.getOrThrow
import io.radio.shared.base.isNotEmpty
import io.radio.shared.base.toOptional
import io.radio.shared.base.viewmodel.ViewModel
import io.radio.shared.domain.date.DateProvider
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.domain.player.PlayerController
import io.radio.shared.domain.player.StreamMetaData
import io.radio.shared.domain.resources.AppResources
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessParams
import io.radio.shared.domain.usecases.track.TrackMediaInfoProcessUseCase
import io.radio.shared.domain.usecases.track.TrackSeekUseCase
import io.radio.shared.domain.usecases.track.TrackUpdatePositionUseCase
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaState
import io.radio.shared.model.TrackMediaTimeLine
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerViewModel(
    appResources: AppResources,
    private val playerController: PlayerController,
    private val trackMediaInfoProcessUseCase: TrackMediaInfoProcessUseCase,
    private val trackUpdatePositionUseCase: TrackUpdatePositionUseCase,
    private val dateProvider: DateProvider,
    private val trackSeekUseCase: TrackSeekUseCase,
    private val trackFormatter: TrackFormatter
) : ViewModel() {


    val trackFlow: Flow<TrackItem> =
        playerController.observeTrackInfo().mapNotNull { it.data?.track }.distinctUntilChanged()

    val trackMediaStateFlow: Flow<Optional<TrackMediaState>> =
        playerController.observeTrackInfo().map { it.data?.state.toOptional() }

    val subTitleFlow = playerController.observeStreamMetaData()
        .combine(trackFlow, subTitleCombiner())

    val playerMetadataFlow = playerController.observePlayerMetaData()

    private val scrubbingTimeFormattedChannel = BroadcastChannel<String>(1)
    val scrubbingTimeFormattedFlow: Flow<String> get() = scrubbingTimeFormattedChannel.asFlow()

    val trackTimeLineFlow: Flow<Optional<TrackMediaTimeLine>> =
        playerController.observeTrackTimeLine()

    private val seekResultChannel = ConflatedBroadcastChannel<Optional<SeekResult>>()
    val seekResultFlow: Flow<Optional<SeekResult>> get() = seekResultChannel.asFlow()

//    private val visualizationColorChannel = ConflatedBroadcastChannel(appResources.accentColor)
//    val visualizationColorFlow: Flow<Int> get() = visualizationColorChannel.asFlow()

    private val seekJobRunner = JobRunner()
    private var enableSeeking = false

    init {
//        trackFlow.onEach { track -> updateVisualizationColor(track) }
//            .catch { /* todo add handling exception */ }
//            .launchIn(scope)
        playerMetadataFlow.onEach {
            enableSeeking = it.data?.enableSeeking == true
        }.launchIn(scope)
    }

    fun onPlayClicked() {
        scope.launch {
            trackMediaInfoProcessUseCase.execute(TrackMediaInfoProcessParams(trackFlow.first()))
        }
    }

    fun onPositionChanged(position: Int, isScrubbing: Boolean) {
        scope.launch {
            scrubbingTimeFormattedChannel.send(
                trackFormatter.formatDuration(
                    trackUpdatePositionUseCase.execute(
                        position,
                        isScrubbing
                    )
                )
            )
        }
    }

    fun rewind() = seek(false)
    fun forward() = seek(true)

    fun next() = playerController.next()
    fun previous() = playerController.previous()

    private fun seek(isForward: Boolean) {
        seekJobRunner.runAndCancelPrevious {
            //todo error handler
            scope.launch(context = CoroutineExceptionHandler { throwable -> }) {
                if (enableSeeking) {
                    val duration = trackSeekUseCase.execute(isForward)
                    if (duration.isNotEmpty()) {
                        val formatted = dateProvider.formatSec(duration.getOrThrow())
                        seekResultChannel.send(
                            (if (isForward) {
                                SeekResult.Forward(formatted)
                            } else {
                                SeekResult.Rewind(formatted)
                            }).toOptional()
                        )
                        delay(SEEK_DELAY)
                        seekResultChannel.send(Optional.empty())
                    }
                }
            }
        }
    }

//    private suspend fun updateVisualizationColor(track: TrackItem) {
//        withContext(IoDispatcher + CoroutineExceptionHandler { throwable ->  /* todo add handling error */ }) {
//            val defaultColor = visualizationColorChannel.value
//            val visualizationColor = track.cover.data?.img?.takeIf { it.isNotEmpty() }?.let {
//                imageProcessor.getDominantColor(it, defaultColor)
//            } ?: defaultColor
//            visualizationColorChannel.send(visualizationColor)
//        }
//    }

    private fun subTitleCombiner(): suspend (streamOpt: Optional<StreamMetaData>, track: TrackItem) -> String =
        { streamOpt, track ->
            if (track.source.isStream) {
                streamOpt.data?.title.orEmpty()
            } else {
                track.subTitle
            }
        }


    sealed class SeekResult {
        class Forward(val timeOffset: String) : SeekResult()
        class Rewind(val timeOffset: String) : SeekResult()
    }

    private companion object {
        const val SEEK_DELAY = 1000L
    }
}