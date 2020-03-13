package io.radio.presentation.stations

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.radio.R
import io.radio.di
import io.radio.shared.common.Logger
import io.radio.shared.presentation.fragment.BaseFragment

class StationsFragment : BaseFragment(R.layout.fragment_stations) {

    private val viewModel: StationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@StationsFragment) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stationLiveData.observe(viewLifecycleOwner, Observer {
            Logger.d("$it")
        })
    }


}