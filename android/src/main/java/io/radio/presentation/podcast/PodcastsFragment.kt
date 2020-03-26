package io.radio.presentation.podcast

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.transition.Hold
import io.radio.R
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routeDetails
import io.radio.shared.base.Logger
import io.radio.shared.base.State
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.viewmodel.koin.viewModels
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.view.SizeScrollOffsetListener
import io.radio.shared.view.addScrollOffsetListener
import io.radio.shared.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts) {

    private val viewModel: PodcastsViewModel by viewModels()
    private var selectedExtra: WeakReference<Navigator.Extras>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val podcastsAdapter = PodcastsAdapter(resources) { _, _, pos, item, extra ->
            (parentFragment as PodcastsSharedElementSupport).setSelectedPos(pos.value)
            selectedExtra = WeakReference(FragmentNavigatorExtras(extra.asTransitionExtraPair()))
            viewModel.onPodcastSelected(item)
        }

        radioPodcastRecycleView.adapter = podcastsAdapter
        radioPodcastRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        viewModel.openPodcastFlow.onEach {
            when (it) {
                is State.Success -> {
                    it.result.performContentIfNotHandled { params ->
                        routeDetails(params, selectedExtra?.get())
                    }
                }
                is State.Fail -> {

                }
                State.Loading -> {

                }
            }
        }.launchIn(viewScope)

        viewModel.podcastsFlow.onEach {
            Logger.d("Podcasts new state: $it")
            when (it) {
                is State.Success -> {
                    progressBar.isVisible = false
                    radioPodcastRecycleView.isVisible = true
                    podcastsAdapter.submitList(it.result)
                }
                is State.Fail -> {
                    progressBar.isVisible = false
                    radioPodcastRecycleView.isVisible = false
                    TODO()
                }
                State.Loading -> {
                    progressBar.isVisible = true
                    radioPodcastRecycleView.isVisible = true
                }
            }
        }.launchIn(viewScope)
    }

    override fun onResume() {
        super.onResume()
        radioPodcastRecycleView.updateScrollOffsetListener()
    }

}