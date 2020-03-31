package io.radio.presentation.stations

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import io.radio.R
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routePlayer
import io.radio.shared.base.Logger
import io.radio.shared.base.State
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.viewmodel.koin.viewModels
import io.radio.shared.presentation.stations.StationsViewModel
import io.radio.shared.view.SizeScrollOffsetListener
import io.radio.shared.view.addScrollOffsetListener
import io.radio.shared.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_stations.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StationsFragment : BaseFragment(R.layout.fragment_stations) {

    private val viewModel: StationsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsAdapter = StationsAdapter { _, _, _, item, _ ->
            viewModel.onStationClicked(item)
        }
        radioStationsRecycleView.adapter = stationsAdapter
        radioStationsRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        viewModel.openStationStateFlow.subscribe {
            when (it) {
                is State.Success -> {
                    it.result.performContentIfNotHandled {
                        routePlayer()
                    }
                }
                is State.Fail -> {
                    //TODO
                }
            }
        }

        viewModel.stationsFlow
            .onEach {
                Logger.d("Stations new state: $it")
                when (it) {
                    is State.Success -> {
                        radioStationsRecycleView.isVisible = true
                        progressBar.isVisible = false
                        stationsAdapter.submitList(it.result)
                    }
                    is State.Fail -> {
                        radioStationsRecycleView.isVisible = false
                        progressBar.isVisible = false
                        //TODO
                    }
                    is State.Loading -> {
                        radioStationsRecycleView.isVisible = false
                        progressBar.isVisible = true
                    }
                }
            }
            .launchIn(viewScope)
    }

    override fun onResume() {
        super.onResume()
        radioStationsRecycleView.updateScrollOffsetListener()
    }

}