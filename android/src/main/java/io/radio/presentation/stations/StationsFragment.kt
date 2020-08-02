package io.radio.presentation.stations

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import io.radio.R
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routePlayer
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.showSnackbar
import io.radio.shared.base.mvi.MviViewDelegate
import io.radio.shared.base.viewmodel.koin.viewBinder
import io.radio.shared.presentation.stations.StationView
import io.radio.shared.presentation.stations.StationView.*
import io.radio.shared.presentation.stations.StationViewBinder
import io.radio.shared.view.SizeScrollOffsetListener
import io.radio.shared.view.addScrollOffsetListener
import io.radio.shared.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_stations.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class StationsFragment : BaseFragment(R.layout.fragment_stations) {

    private val viewBinder by viewBinder<StationViewBinder>()
    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)
    private val mviView = object : MviViewDelegate<Intent, Model, Event>(
        this@StationsFragment,
        ::bindIntents,
        ::render,
        ::event
    ), StationView {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsAdapter = StationsAdapter { _, _, _, item, _ ->
            adapterIntentsChannel.offer(Intent.SelectStation(item))
        }
        radioStationsRecycleView.adapter = stationsAdapter
        radioStationsRecycleView.tag = stationsAdapter
        radioStationsRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        viewScope.launch { viewBinder.attachView(mviView) }
    }

    override fun onResume() {
        super.onResume()
        radioStationsRecycleView.updateScrollOffsetListener()
    }

    private fun bindIntents(): Flow<Intent> {
        return adapterIntentsChannel.asFlow()
    }

    private fun render(model: Model) = with(model) {
        progressBar.isVisible = isLoading
        radioStationsRecycleView.isVisible = !isLoading
        (radioStationsRecycleView.tag as StationsAdapter).submitList(data)
    }

    private fun event(event: Event) {
        when (event) {
            is Event.Error -> showSnackbar(event.message)
            Event.NavigateToPlayer -> routePlayer()
        }
    }

}