package io.shared.model

data class PodcastDetails(
    val name: String,
    val cover: String,
    val items: List<RadioPodcastDetailsItem>
)