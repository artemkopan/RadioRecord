package io.radio.shared.network

import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.radio.shared.configs.NetworkConfiguration
import io.radio.shared.mapper.RadioPodcastDetailsMapper
import io.radio.shared.mapper.RadioPodcastMapper
import io.radio.shared.mapper.RadioStationMapper
import io.radio.shared.model.Podcast
import io.radio.shared.model.PodcastDetails
import io.radio.shared.model.Station
import io.radio.shared.network.reponse.DataResultResponse
import io.radio.shared.network.reponse.RadioPodcastDetailsResponse
import io.radio.shared.network.reponse.RadioPodcastResponse
import io.radio.shared.network.reponse.RadioStationResponse


interface ApiSource {

    suspend fun getStations(): List<Station>

    suspend fun getPodcasts(): List<Podcast>

    suspend fun getPodcastById(id: Int): PodcastDetails

}

class ApiSourceImpl constructor(
    private val networkConfiguration: NetworkConfiguration,
    private val radioStationMapper: RadioStationMapper,
    private val radioPodcastMapper: RadioPodcastMapper,
    private val radioPodcastDetailsMapper: RadioPodcastDetailsMapper
) : ApiSource {

    private val urlBuilder get() = URLBuilder(API_URL)

    override suspend fun getStations(): List<Station> {
        return networkConfiguration.httpClient.get<DataResultResponse<List<RadioStationResponse>>>(
                url = urlBuilder.path("radioapi/stations").build()
            )
            .result
            .let { radioStationMapper.mapList(it) }
    }

    override suspend fun getPodcasts(): List<Podcast> {
        return networkConfiguration.httpClient.get<DataResultResponse<List<RadioPodcastResponse>>>(
                url = urlBuilder.path("radioapi/podcasts/").build()
            )
            .result
            .let { radioPodcastMapper.mapList(it) }
    }

    override suspend fun getPodcastById(id: Int): PodcastDetails {
        return networkConfiguration.httpClient.get<DataResultResponse<RadioPodcastDetailsResponse>>(
            url = urlBuilder.path("radioapi/podcast/")
                .also {
                    it.parameters.append("id", id.toString())
                }
                .build()
        ).result.let { radioPodcastDetailsMapper.map(it) }
    }

    companion object {
        private const val API_URL = "http://www.radiorecord.ru"
    }
}