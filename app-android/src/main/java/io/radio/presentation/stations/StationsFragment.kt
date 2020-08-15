package io.radio.presentation.stations

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.showToast
import io.radio.di.binder.viewBinder
import io.radio.extensions.parseResourceString
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routePlayer
import io.radio.views.SizeScrollOffsetListener
import io.radio.views.addScrollOffsetListener
import io.radio.views.updateScrollOffsetListener
import io.shared.mvi.bind
import io.shared.presentation.stations.StationView
import io.shared.presentation.stations.StationView.*
import io.shared.presentation.stations.StationViewBinder
import kotlinx.android.synthetic.main.fragment_stations.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class StationsFragment : BaseFragment(R.layout.fragment_stations), StationView {

    private val viewBinder by viewBinder<StationViewBinder>()
    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsAdapter = StationsAdapter { _, _, _, item, _ ->
            adapterIntentsChannel.offer(Intent.SelectStation(item))
        }
        stationsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        radioStationsRecycleView.adapter = stationsAdapter
        radioStationsRecycleView.tag = stationsAdapter
        radioStationsRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        this bind viewBinder
    }

    override fun onResume() {
        super.onResume()
        radioStationsRecycleView.updateScrollOffsetListener()
    }

    override val intents: Flow<Intent>
        get() = adapterIntentsChannel.asFlow()

    override fun render(model: Model) = with(model) {
        progressBar.isVisible = isLoading
        radioStationsRecycleView.isVisible = !isLoading
        (radioStationsRecycleView.tag as StationsAdapter).submitList(data)
    }

    override fun acceptEffect(effect: Effect) {
        when (effect) {
            is Effect.Error -> showToast(parseResourceString(effect.message))
            Effect.NavigateToPlayer -> routePlayer()
        }
    }

}