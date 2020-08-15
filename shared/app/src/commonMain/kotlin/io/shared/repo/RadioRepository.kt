package io.shared.repo

import io.shared.core.IoDispatcher
import io.shared.model.Podcast
import io.shared.model.PodcastDetails
import io.shared.model.Station
import io.shared.network.RadioApiSource
import kotlinx.coroutines.withContext

interface RadioRepository {

    suspend fun getStations(): List<Station>

    suspend fun getPodcasts(): List<Podcast>

    suspend fun getPodcast(id: Int): PodcastDetails

}

//todo add database implementation
class RadioRepositoryImpl constructor(
    private val radioApiSource: RadioApiSource
) : RadioRepository {

    override suspend fun getStations(): List<Station> = withContext(IoDispatcher) {
        radioApiSource.getStations()
    }

    override suspend fun getPodcasts(): List<Podcast> = withContext(IoDispatcher) {
        radioApiSource.getPodcasts()
    }

    override suspend fun getPodcast(id: Int): PodcastDetails = withContext(IoDispatcher) {
        radioApiSource.getPodcastById(id)
    }

}