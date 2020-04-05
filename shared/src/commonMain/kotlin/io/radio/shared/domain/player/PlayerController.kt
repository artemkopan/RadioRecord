package io.radio.shared.domain.player

import io.radio.shared.base.Optional
import io.radio.shared.model.TrackItem
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaTimeLine
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlayerController {

    fun observeTrackInfo(): Flow<Optional<TrackMediaInfo>>

    fun observeTrackTimeLine(): Flow<Optional<TrackMediaTimeLine>>

    fun observePlayerMetaData(): Flow<Optional<PlayerMetaData>>

    fun observeStreamMetaData(): Flow<Optional<StreamMetaData>>

    fun prepare(trackItem: TrackItem, autoPlay: Boolean)

    fun release()

    fun destroy()

    fun setPosition(position: Duration)

    fun seekTo(offset: Duration)

    fun play()

    fun pause()

}


