package io.radio.shared.model

import kotlin.time.Duration

data class TrackItem(
    val id: Int,
    val title: String,
    val subTitle: String,
    val duration: Duration,
    val source: TrackSource
)


sealed class TrackSource {

    data class Hls(val link: String) : TrackSource()
    data class Progressive(val link: String) : TrackSource()

}