package io.radio.presentation.podcast.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.transition.Hold
import io.radio.R
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routeDetails
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.showSnackbar
import io.radio.shared.base.mvi.MviViewDelegate
import io.radio.shared.base.viewmodel.koin.viewBinder
import io.radio.shared.presentation.podcast.home.PodcastView
import io.radio.shared.presentation.podcast.home.PodcastView.*
import io.radio.shared.presentation.podcast.home.PodcastViewBinder
import io.radio.shared.view.SizeScrollOffsetListener
import io.radio.shared.view.addScrollOffsetListener
import io.radio.shared.view.updateScrollOffsetListener
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts) {

    private val viewBinder by viewBinder<PodcastViewBinder>()
    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)
    private val mviView = object : MviViewDelegate<Intent, Model, Event>(
        this@PodcastsFragment,
        ::bindIntents,
        ::render,
        ::event
    ), PodcastView {}

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
            adapterIntentsChannel.offer(Intent.SelectPodcast(item))
        }

        radioPodcastRecycleView.adapter = podcastsAdapter
        radioPodcastRecycleView.tag = podcastsAdapter
        radioPodcastRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        viewScope.launch { viewBinder.attachView(mviView) }
    }

    override fun onResume() {
        super.onResume()
        radioPodcastRecycleView.updateScrollOffsetListener()
    }

    fun bindIntents(): Flow<Intent> {
        return adapterIntentsChannel.asFlow()
    }

    private fun render(model: Model) = with(model) {
        progressBar.isVisible = isLoading
        radioPodcastRecycleView.isVisible = !isLoading
        (radioPodcastRecycleView.tag as PodcastsAdapter).submitList(data) {

        }
    }

    private fun event(event: Event) = with(event) {
        when (this) {
            is Event.Error -> showSnackbar(message)
            is Event.NavigateToDetails -> routeDetails(params, selectedExtra?.get())
        }
    }

}