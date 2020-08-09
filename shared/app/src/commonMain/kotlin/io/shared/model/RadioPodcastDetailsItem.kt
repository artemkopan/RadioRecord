package io.shared.model

data class RadioPodcastDetailsItem(
    val id: Int,
    val time: Int,
    val title: String,
    val artist: String,
    val song: String,
    val playlist: String,
    val link: String
)