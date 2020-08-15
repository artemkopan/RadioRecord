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
import io.shared.network.reponse.RadioStationResponse


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
        return httpClientProvider.httpClient.get<DataResultResponse<List<RadioStationResponse>>>(
            url = httpClientProvider.urlBuilder.path("radioapi/stations").build()
        )
            .result
            .let { radioStationMapper.mapList(it) }
    }

    override suspend fun getPodcasts(): List<Podcast> {
        return httpClientProvider.httpClient.get<DataResultResponse<List<RadioPodcastResponse>>>(
            url = httpClientProvider.urlBuilder.path("radioapi/podcasts/").build()
        )
            .result
            .let { radioPodcastMapper.mapList(it) }
    }

    override suspend fun getPodcastById(id: Int): PodcastDetails {
        return httpClientProvider.httpClient.get<DataResultResponse<RadioPodcastDetailsResponse>>(
            url = httpClientProvider.urlBuilder.path("radioapi/podcast/")
                .apply {
                    parameters.append("id", id.toString())
                }
                .build()
        ).result.let { radioPodcastDetailsMapper.map(it) }
    }

}