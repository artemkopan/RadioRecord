package io.radio.shared.data.repositories.station

import io.radio.shared.base.IoDispatcher
import io.radio.shared.data.network.ApiSource
import io.radio.shared.model.RadioPodcast
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.model.RadioStation
import kotlinx.coroutines.withContext

interface RadioRepository {

    suspend fun getStations(): List<RadioStation>

    suspend fun getPodcasts(): List<RadioPodcast>

    suspend fun getPodcast(id: Int): RadioPodcastDetails

}

//todo add database implementation
class RadioRepositoryImpl constructor(
    private val apiSource: ApiSource
) : RadioRepository {

    override suspend fun getStations(): List<RadioStation> = withContext(IoDispatcher) {
        apiSource.getStations()
    }

    override suspend fun getPodcasts(): List<RadioPodcast> = withContext(IoDispatcher) {
        apiSource.getPodcasts()
    }

    override suspend fun getPodcast(id: Int): RadioPodcastDetails = withContext(IoDispatcher) {
        apiSource.getPodcastById(id)
    }

}