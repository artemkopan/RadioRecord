package io.shared.network.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioPodcastDetailsItemResponse(

	@SerialName("song")
	val song: String? = null,

	@SerialName("playlist")
	val playlist: String? = null,

	@SerialName("artist")
	val artist: String? = null,

	@SerialName("link")
	val link: String? = null,

	@SerialName("image100")
	val image100: String? = null,

	@SerialName("image600")
	val image600: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("time")
	val time: Int? = null
)