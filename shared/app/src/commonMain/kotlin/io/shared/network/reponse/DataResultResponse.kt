package io.shared.network.reponse

import kotlinx.serialization.Serializable

@Serializable
data class DataResultResponse<T>(val result: T)
