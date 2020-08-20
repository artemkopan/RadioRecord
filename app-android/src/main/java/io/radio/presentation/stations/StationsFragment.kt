package io.radio.presentation.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.showToast
import io.radio.databinding.FragmentHomeBinding
import io.radio.databinding.FragmentStationsBinding
import io.radio.di.binder.viewBinder
import io.radio.extensions.parseResourceString
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routePlayer
import io.radio.views.SizeScrollOffsetListener
import io.radio.views.addScrollOffsetListener
import io.radio.views.updateScrollOffsetListener
import io.shared.core.Logger
import io.shared.presentation.stations.StationView
import io.shared.presentation.stations.StationView.*
import io.shared.presentation.stations.StationViewBinder
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.asFlow

class StationsFragment : BaseFragment(R.layout.fragment_stations), StationView {

    private val viewBinder by viewBinder<StationViewBinder>()
    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)

    private val binding: FragmentStationsBinding by viewBinding { fragment ->
        FragmentStationsBinding.bind(fragment.requireView())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stationsAdapter = StationsAdapter { _, _, _, item, _ ->
            adapterIntentsChannel.offer(Intent.SelectStation(item))
        }
        stationsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.radioStationsRecycleView.adapter = stationsAdapter
        binding.radioStationsRecycleView.tag = stationsAdapter
        binding.radioStationsRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        scope.attachBinder(viewBinder)
    }

    override fun onResume() {
        super.onResume()
        binding.radioStationsRecycleView.updateScrollOffsetListener()
    }

    override val intents: Flow<Intent>
        get() = adapterIntentsChannel.asFlow()

    override fun render(model: Model) = with(model) {
        Logger.d("render $model")
        binding.progressBar.isVisible = isLoading
        binding.radioStationsRecycleView.isVisible = !isLoading
        (binding.radioStationsRecycleView.tag as StationsAdapter).submitList(data)
    }

    override fun acceptEffect(effect: Effect) {
        when (effect) {
            is Effect.Error -> showToast(parseResourceString(effect.message))
            Effect.NavigateToPlayer -> routePlayer()
        }
    }

}