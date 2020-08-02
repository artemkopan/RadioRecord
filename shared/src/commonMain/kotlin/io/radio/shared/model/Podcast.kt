package io.radio.shared.model

import io.radio.shared.base.Persistable

data class Podcast(
    val id: Int,
    val name: String,
    val cover: String
) : Persistable