package io.shared.network.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioPodcastDetailsResponse(

	@SerialName("cover")
	val cover: String? = null,

	@SerialName("content_type")
	val contentType: String? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("items")
	val items: List<RadioPodcastDetailsItemResponse>? = null
)