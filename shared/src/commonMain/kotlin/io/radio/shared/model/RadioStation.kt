package io.radio.shared.model

data class RadioStation(
    val id: Int,
    val title:String,
    val icon: String,
    val iconSvg: String,
    val stream: String,
    val stream32:String,
    val stream64:String,
    val stream128:String,
    val stream320:String
)