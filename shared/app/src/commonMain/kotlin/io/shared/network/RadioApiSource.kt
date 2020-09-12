package io.shared.network

import io.ktor.client.request.*
import io.shared.mapper.RadioPodcastDetailsMapper
import io.shared.mapper.RadioPodcastMapper
import io.shared.mapper.RadioStationMapper
import io.shared.model.Podcast
import io.shared.model.PodcastDetails
import io.shared.model.Station
import io.shared.network.reponse.DataResultResponse
import io.shared.network.reponse.RadioPodcastDetailsResponse
import io.shared.network.reponse.RadioPodcastResponse
import io.shared.network.reponse.RadioStationListResponse


interface RadioApiSource {

    suspend fun getStations(): List<Station>

    suspend fun getPodcasts(): List<Podcast>

    suspend fun getPodcastById(id: Int): PodcastDetails

}

class RadioApiSourceImpl constructor(
    private val httpClientProvider: HttpClientProvider,
    private val radioStationMapper: RadioStationMapper,
    private val radioPodcastMapper: RadioPodcastMapper,
    private val radioPodcastDetailsMapper: RadioPodcastDetailsMapper
) : RadioApiSource {

    override suspend fun getStations(): List<Station> {
        return httpClientProvider.client.get<DataResultResponse<RadioStationListResponse>>(
            url = httpClientProvider.urlBuilder.path("api/stations").build()
        )
            .result
            .let { radioStationMapper.mapList(it.stations) }
    }

    override suspend fun getPodcasts(): List<Podcast> {
        return httpClientProvider.client.get<DataResultResponse<List<RadioPodcastResponse>>>(
            url = httpClientProvider.urlBuilder.path("api/podcasts/").build()
        )
            .result
            .let { radioPodcastMapper.mapList(it) }
    }

    override suspend fun getPodcastById(id: Int): PodcastDetails {
        return httpClientProvider.client.get<DataResultResponse<RadioPodcastDetailsResponse>>(
            url = httpClientProvider.urlBuilder.path("api/podcast/")
                .apply {
                    parameters.append("id", id.toString())
                }
                .build()
        ).result.let { radioPodcastDetailsMapper.map(it) }
    }

}