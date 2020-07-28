package io.radio.feature.podcast

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.transition.Hold
import io.radio.R
import io.radio.feature.home.postScrolledFraction
import io.radio.presentation.routeDetails
import io.radio.shared.base.Logger
import io.radio.shared.base.State
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.viewmodel.koin.viewModel
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.view.SizeScrollOffsetListener
import io.radio.shared.view.addScrollOffsetListener
import io.radio.shared.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts) {

    private val viewModel: PodcastsViewModel by viewModel()
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
                    it.result.consume { params ->
                        routeDetails(params, selectedExtra?.get())
                    }
                }
                is State.Fail -> {

                }
                State.Loading -> {

                }
            }
        }.launchIn(scope)

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
        }.launchIn(scope)
    }

    override fun onResume() {
        super.onResume()
        radioPodcastRecycleView.updateScrollOffsetListener()
    }

}