package io.radio.shared.data.network.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioStationResponse(

    @SerialName("song")
    val song: String? = null,

    @SerialName("new")
    val jsonMemberNew: Boolean? = null,

    @SerialName("has_feedback")
    val hasFeedback: Boolean? = null,

    @SerialName("artist")
    val artist: String? = null,

    @SerialName("prefix")
    val prefix: String? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("icon_png")
    val iconPng: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("feedback_text")
    val feedbackText: String? = null,

    @SerialName("stream_32")
    val stream32: String? = null,

    @SerialName("schedule")
    val schedule: String? = null,

    @SerialName("stream_64")
    val stream64: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("stream")
    val stream: String? = null,

    @SerialName("stream_128")
    val stream128: String? = null,

    @SerialName("stream_320")
    val stream320: String? = null
)