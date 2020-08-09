package io.shared.repo

import io.shared.core.IoDispatcher
import io.shared.model.Podcast
import io.shared.model.PodcastDetails
import io.shared.model.Station
import io.shared.network.ApiSource
import kotlinx.coroutines.withContext

interface RadioRepository {

    suspend fun getStations(): List<Station>

    suspend fun getPodcasts(): List<Podcast>

    suspend fun getPodcast(id: Int): PodcastDetails

}

//todo add database implementation
class RadioRepositoryImpl constructor(
    private val apiSource: ApiSource
) : RadioRepository {

    override suspend fun getStations(): List<Station> = withContext(IoDispatcher) {
        apiSource.getStations()
    }

    override suspend fun getPodcasts(): List<Podcast> = withContext(IoDispatcher) {
        apiSource.getPodcasts()
    }

    override suspend fun getPodcast(id: Int): PodcastDetails = withContext(IoDispatcher) {
        apiSource.getPodcastById(id)
    }

}