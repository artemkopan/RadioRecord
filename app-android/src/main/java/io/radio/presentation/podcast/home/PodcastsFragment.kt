package io.radio.presentation.podcast.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.Hold
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.showToast
import io.radio.di.binder.viewBinder
import io.radio.extensions.parseResourceString
import io.radio.presentation.home.postScrolledFraction
import io.radio.presentation.routeDetails
import io.radio.views.SizeScrollOffsetListener
import io.radio.views.addScrollOffsetListener
import io.radio.views.updateScrollOffsetListener
import io.shared.presentation.podcast.home.PodcastView
import io.shared.presentation.podcast.home.PodcastView.*
import io.shared.presentation.podcast.home.PodcastViewBinder
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.lang.ref.WeakReference

class PodcastsFragment : BaseFragment(R.layout.fragment_podcasts), PodcastView {

    private val viewBinder by viewBinder<PodcastViewBinder>()
    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)

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
        podcastsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        radioPodcastRecycleView.adapter = podcastsAdapter
        radioPodcastRecycleView.tag = podcastsAdapter
        radioPodcastRecycleView.addScrollOffsetListener(
            SizeScrollOffsetListener(
                resources.getDimensionPixelOffset(R.dimen.headerElevationOffset).toFloat()
            ) { postScrolledFraction(it) }
        )

        scope.attachBinder(viewBinder)
    }

    override fun onResume() {
        super.onResume()
        radioPodcastRecycleView.updateScrollOffsetListener()
    }

    override val intents: Flow<Intent>
        get() = adapterIntentsChannel.asFlow()

    override fun render(model: Model) = with(model) {
        progressBar.isVisible = isLoading
        radioPodcastRecycleView.isVisible = !isLoading
        (radioPodcastRecycleView.tag as PodcastsAdapter).submitList(data)
    }

    override fun acceptEffect(effect: Effect) = with(effect) {
        when (this) {
            is Effect.Error -> showToast(parseResourceString(message))
            is Effect.NavigateToDetails -> routeDetails(params, selectedExtra?.get())
        }
    }

}