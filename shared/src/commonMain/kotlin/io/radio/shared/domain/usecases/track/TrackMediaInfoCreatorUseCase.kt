package io.radio.shared.domain.usecases.track

import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.UseCase
import io.radio.shared.domain.formatters.TrackFormatter
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaState
import kotlinx.coroutines.withContext
import kotlin.time.Duration

class TrackMediaInfoCreatorUseCase(private val trackFormatter: TrackFormatter) :
    UseCase<TrackMediaInfoCreatorUseCase.Params, TrackMediaInfo> {

    override suspend fun execute(params: Params): TrackMediaInfo = withContext(IoDispatcher) {
        withContext(IoDispatcher) {
            TrackMediaInfo(
                params.track,
                params.state,
                trackFormatter.formatDuration(params.duration ?: params.track.duration)
            )
        }
    }

    class Params(
        val track: TrackItem,
        val state: TrackMediaState = TrackMediaState.None,
        val duration: Duration? = null
    )
}