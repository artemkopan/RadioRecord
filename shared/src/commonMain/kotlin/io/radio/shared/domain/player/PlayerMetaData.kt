package io.radio.shared.domain.player

data class PlayerMetaData(
    val sessionId: Int,
    val enableSeeking: Boolean,
    val enablePrevious: Boolean,
    val enableRewind: Boolean,
    val enableFastForward: Boolean,
    val enableNext: Boolean
)

data class StreamMetaData(val title: String)