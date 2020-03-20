package io.radio.presentation.station

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import io.radio.R
import io.radio.di
import io.radio.presentation.home.postScrolledFraction
import io.radio.shared.common.Logger
import io.radio.shared.common.State
import io.radio.shared.presentation.fragment.BaseFragment
import io.radio.shared.presentation.view.SizeScrollOffsetListener
import io.radio.shared.presentation.view.addScrollOffsetListener
import io.radio.shared.presentation.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_stations.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StationsFragment : BaseFragment(R.layout.fragment_stations) {

    private val viewModel: StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@StationsFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsAdapter = StationsAdapter()
        radioStationsRecycleView.adapter = stationsAdapter
        radioStationsRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

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