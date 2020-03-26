package io.radio.shared.presentation.podcast.details

import io.radio.shared.base.Parcelable
import io.radio.shared.base.Parcelize

@Parcelize
data class PodcastDetailsParams(
    val id: Int,
    val name: String,
    val cover: String,
    val headerColor: Int,
    val toolbarColor: Int
) : Parcelable