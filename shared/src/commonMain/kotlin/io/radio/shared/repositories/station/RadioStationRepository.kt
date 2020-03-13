package io.radio.shared.repositories.station

import io.radio.shared.common.Inject
import io.radio.shared.common.IoDispatcher
import io.radio.shared.model.RadioPodcast
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.model.RadioStation
import io.radio.shared.network.ApiSource
import kotlinx.coroutines.withContext

interface RadioStationRepository {

    suspend fun getStations(): List<RadioStation>

    suspend fun getPodcasts(): List<RadioPodcast>

    suspend fun getPodcast(id: Int): RadioPodcastDetails

}

//todo add database implementation
class RadioStationRepositoryImpl @Inject constructor(
    private val apiSource: ApiSource
) : RadioStationRepository {

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