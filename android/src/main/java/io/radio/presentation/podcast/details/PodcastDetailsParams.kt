package io.radio.presentation.podcast.details

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PodcastDetailsParams(
    val id: Int,
    val cover: String,
    val headerColor: Int,
    val toolbarColor: Int
) : Parcelable