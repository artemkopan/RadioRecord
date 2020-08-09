package io.shared.presentation.podcast.details

import io.shared.core.Parcelable
import io.shared.core.Parcelize

@Parcelize
data class PodcastDetailsParams(
    val id: Int,
    val name: String,
    val cover: String,
    val headerColor: Int,
    val toolbarColor: Int
) : Parcelable