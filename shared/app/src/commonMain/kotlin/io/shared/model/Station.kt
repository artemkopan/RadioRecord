package io.shared.model

data class Station(
    val id: Int,
    val title: String,
    val iconGray: String,
    val iconWhite: String,
    val stream64: String,
    val stream128: String,
    val stream320: String
)