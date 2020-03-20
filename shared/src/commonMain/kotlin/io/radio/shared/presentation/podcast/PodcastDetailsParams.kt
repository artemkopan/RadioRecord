package io.radio.shared.presentation.podcast

import io.radio.shared.common.Parcelable
import io.radio.shared.common.Parcelize

@Parcelize
data class PodcastDetailsParams(
    val id: Int,
    val cover: String,
    val headerColor: Int,
    val toolbarColor: Int
) : Parcelable