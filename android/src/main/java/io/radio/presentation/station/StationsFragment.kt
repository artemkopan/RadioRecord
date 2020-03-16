package io.radio.presentation.station

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import io.radio.R
import io.radio.di
import io.radio.shared.common.Logger
import io.radio.shared.presentation.State
import io.radio.shared.presentation.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_stations.*

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

        viewModel.stationLiveData.observe(viewLifecycleOwner, Observer {
            Logger.d("$it")
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
        })
    }

}