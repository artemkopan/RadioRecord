package io.radio.shared.network

import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.utils.io.errors.IOException
import io.radio.shared.common.Inject
import io.radio.shared.mapper.RadioPodcastDetailsMapper
import io.radio.shared.mapper.RadioPodcastMapper
import io.radio.shared.mapper.RadioStationMapper
import io.radio.shared.model.RadioPodcast
import io.radio.shared.model.RadioPodcastDetails
import io.radio.shared.model.RadioStation
import io.radio.shared.network.reponse.DataResultResponse
import io.radio.shared.network.reponse.RadioPodcastDetailsResponse
import io.radio.shared.network.reponse.RadioPodcastResponse
import io.radio.shared.network.reponse.RadioStationResponse


interface RestApiService {

    suspend fun getStations(): List<RadioStation>

    suspend fun getPodcasts(): List<RadioPodcast>

    suspend fun getPodcastById(id: Int): RadioPodcastDetails

}

class RestApiServiceImpl @Inject constructor(
    private val networkConfiguration: NetworkConfiguration,
    private val radioStationMapper: RadioStationMapper,
    private val radioPodcastMapper: RadioPodcastMapper,
    private val radioPodcastDetailsMapper: RadioPodcastDetailsMapper
) : RestApiService {

    private val urlBuilder get() = URLBuilder(API_URL)

    override suspend fun getStations(): List<RadioStation> {
        return networkConfiguration.httpClient.get<DataResultResponse<List<RadioStationResponse>>>(
                url = urlBuilder.path("radioapi/stations").build()
            )
            .result
            .orEmpty()
            .let { radioStationMapper.mapList(it) }
    }

    override suspend fun getPodcasts(): List<RadioPodcast> {
        return networkConfiguration.httpClient.get<DataResultResponse<List<RadioPodcastResponse>>>(
                url = urlBuilder.path("radioapi/podcasts").build()
            )
            .result
            .orEmpty()
            .let { radioPodcastMapper.mapList(it) }
    }

    override suspend fun getPodcastById(id: Int): RadioPodcastDetails {
        return networkConfiguration.httpClient.get<DataResultResponse<RadioPodcastDetailsResponse>>(
            url = urlBuilder.path("radioapi/podcasts")
                .also {
                    it.parameters.append("id", id.toString())
                }
                .build()
        ).result?.let { radioPodcastDetailsMapper.map(it) }
            ?: throw IOException("Podcast's details by id $id is failed to load")
    }

    companion object {
        private const val API_URL = "http://www.radiorecord.ru"
    }
}