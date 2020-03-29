package io.radio.shared.model

import io.radio.shared.base.Optional
import kotlin.time.Duration

data class TrackItem(
    val id: Int,
    val title: String,
    val subTitle: String,
    val cover: Optional<CoverImage>,
    val duration: Duration,
    val source: TrackSource
)


sealed class TrackSource {

    data class Hls(val link: String) : TrackSource()
    data class Progressive(val link: String) : TrackSource()

}

inline class CoverImage(val img: String)