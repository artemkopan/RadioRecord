package io.radio

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import io.radio.shared.common.IoDispatcher
import io.radio.shared.network.RestApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : FragmentActivity(R.layout.main_activity) {

    @Inject
    lateinit var restApiService: RestApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@MainActivity) }

        GlobalScope.launch(IoDispatcher) {
            restApiService.getStations()
            restApiService.getPodcastById(restApiService.getPodcasts().first().id)
        }

    }

}