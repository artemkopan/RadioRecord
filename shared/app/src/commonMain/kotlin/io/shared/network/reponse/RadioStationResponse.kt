package io.shared.network.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioStationListResponse(val stations: List<RadioStationResponse>)

@Serializable
data class RadioStationResponse(

    @SerialName("new")
    val jsonMemberNew: Boolean? = null,

    @SerialName("prefix")
    val prefix: String? = null,

    @SerialName("icon_fill_white")
    val iconFillWhite: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("icon_gray")
    val iconGray: String? = null,

    @SerialName("icon_fill_colored")
    val iconFillColored: String? = null,

    @SerialName("short_title")
    val shortTitle: String? = null,

    @SerialName("stream_64")
    val stream64: String? = null,

    @SerialName("stream_128")
    val stream128: String? = null,

    @SerialName("shareUrl")
    val shareUrl: String? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("stream_320")
    val stream320: String? = null

)