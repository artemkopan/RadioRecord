package io.radio.shared.model

import io.shared.core.Persistable

data class Podcast(
    val id: Int,
    val name: String,
    val cover: String
) : Persistable