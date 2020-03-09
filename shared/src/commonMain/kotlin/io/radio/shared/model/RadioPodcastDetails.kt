package io.radio.shared.model

data class RadioPodcastDetails(
    val name: String,
    val cover: String,
    val items: List<RadioPodcastDetailsItem>
)