package io.shared.network.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RadioPodcastResponse(

    @SerialName("cover_vertical")
    val coverVertical: String? = null,

    @SerialName("cover_horizontal")
    val coverHorizontal: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: Int? = null

)