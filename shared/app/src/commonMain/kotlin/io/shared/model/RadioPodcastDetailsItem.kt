package io.shared.model

data class RadioPodcastDetailsItem(
    val id: Int,
    val time: Int,
    val artist: String,
    val song: String,
    val playlist: String,
    val link: String,
    val image100: String,
    val image600: String
)