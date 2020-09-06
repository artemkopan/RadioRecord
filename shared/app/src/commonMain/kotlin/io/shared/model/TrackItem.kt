@file:Suppress("NOTHING_TO_INLINE")

package io.shared.model

import io.shared.core.Loggable
import io.shared.core.Optional
import kotlin.time.Duration

data class TrackItem(
    val id: Int,
    val title: String,
    val subTitle: String,
    val cover: Optional<CoverImage>,
    val duration: Duration,
    val source: TrackSource
) : Loggable {
    override fun toLogMessage(): String {
        return "TrackItem(id=$id, title='$title', subTitle='$subTitle', cover=${cover.data?.img}, duration=$duration, source=${source.getLinkFromSource()})"
    }
}


sealed class TrackSource {

    data class Hls(val link: String) : TrackSource()
    data class Progressive(val link: String) : TrackSource()
    data class ProgressiveStream(val link: String) : TrackSource()

    inline fun getLinkFromSource(): String {
        return when (this) {
            is Hls -> link
            is Progressive -> link
            is ProgressiveStream -> link
        }
    }

    inline val isStream
        get() = when (this) {
            is Hls,
            is ProgressiveStream -> true
            else -> false
        }
}

inline class CoverImage(val img: String)