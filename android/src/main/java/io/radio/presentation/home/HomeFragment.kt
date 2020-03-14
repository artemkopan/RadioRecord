package io.radio.presentation.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.radio.R
import io.radio.di
import io.radio.shared.common.Logger
import io.radio.shared.presentation.State
import io.radio.shared.presentation.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@HomeFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stationsAdapter = StationsAdapter()
        radioStationsRecycleView.adapter = stationsAdapter

        val podcastsAdapter = PodcastAdapter()
        radioPodcastRecycleView.adapter = podcastsAdapter

        viewModel.stationLiveData.observe(viewLifecycleOwner, Observer {
            Logger.d("$it")
            when (it) {
                is State.Success -> stationsAdapter.submitList(it.result)
                is State.Fail -> TODO()
                State.Loading -> {
                }
            }
        })

        viewModel.podcastsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is State.Success -> podcastsAdapter.submitList(it.result)
                is State.Fail -> TODO()
                State.Loading -> {

                }
            }
        })
    }


}