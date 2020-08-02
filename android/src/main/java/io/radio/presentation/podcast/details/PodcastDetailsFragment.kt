package io.radio.presentation.podcast.details

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.presentation.podcast.details.track.TracksAdapter
import io.radio.presentation.routePlayer
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.doOnFinallyImageCallback
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.BlurTransformation
import io.radio.shared.base.imageloader.transformations.CircleTransformation
import io.radio.shared.base.imageloader.transformations.GranularRoundedCornersTransformation
import io.radio.shared.base.mvi.MviViewDelegate
import io.radio.shared.base.viewmodel.koin.viewBinder
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.presentation.podcast.details.PodcastDetailsVewBinder
import io.radio.shared.presentation.podcast.details.PodcastDetailsView
import io.radio.shared.presentation.podcast.details.PodcastDetailsView.*
import io.radio.shared.presentation.podcast.details.TrackPositionScrollerHelper
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch


class PodcastDetailsFragment : BaseFragment(R.layout.fragment_podcast_details) {

    private val viewBinder by viewBinder<PodcastDetailsVewBinder>()

    private val adapterIntentsChannel = BroadcastChannel<Intent>(1)
    private val mviView = object : MviViewDelegate<Intent, Model, Event>(
        savedStateRegistryOwner = this@PodcastDetailsFragment,
        intentFlow = ::bindIntents,
        onRender = ::render,
        onEvent = ::event
    ), PodcastDetailsView {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (names == null || sharedElements == null) return
                sharedElements[names.first()] = podcastDetailsCoverView
            }
        })
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialFadeThrough()
        exitTransition = returnTransition

        postponeEnterTransition()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = PodcastDetailsFragmentArgs.fromBundle(requireArguments()).params

        podcastDetailsToolbar.navigationIcon?.setTint(params.toolbarColor)
        podcastDetailsToolbar.setNavigationOnClickListener { popBack() }
        podcastDetailsHeader.setCardBackgroundColor(params.headerColor)
        podcastDetailsHeader.doOnPreDraw {
            loadHeader(params)
            loadCover(params) {
                startPostponedEnterTransition()
            }
        }
        podcastDetailsTitle.setTextColor(params.toolbarColor)
        podcastDetailsTitle.text = params.name

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(podcastDetailsHeader, insets)
            ViewCompat.onApplyWindowInsets(podcastDetailsToolbar, insets)
            ViewCompat.onApplyWindowInsets(podcastDetailsCoverView, insets)
            insets
        }

        initTracksAdapter()
        initTrackPositionHandler()

    }

    override fun onStart() {
        super.onStart()
        viewScope.launch { viewBinder.attachView(mviView) }
    }


    private fun bindIntents(): Flow<Intent> {
        return adapterIntentsChannel.asFlow()
    }

    private fun render(model: Model) {
        (podcastTracksRecycler.adapter as TracksAdapter).run {
            submitList(model.tracksWithState)
            stateRestorationPolicy = StateRestorationPolicy.ALLOW
        }

        (podcastTracksRecycler.tag as? TrackPositionScrollerHelper)?.run {
            model.playlist?.position?.let(::onTrackChanged)
        }
//        loadHeader(model)
//        loadCover(model) { startPostponedEnterTransition() }
    }

    private fun event(event: Event) = with(event) {
        when (this) {
            Event.NavigateToPlayer -> routePlayer()
            is Event.Error -> {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun initTracksAdapter() {
        val tracksAdapter =
            TracksAdapter { _, viewId, _, trackMediaInfo, _ ->
                when (viewId.id) {
                    R.id.playButton -> adapterIntentsChannel.offer(
                        Intent.PlayPauseClick(
                            trackMediaInfo.track
                        )
                    )
                    R.id.trackContainerLayout -> adapterIntentsChannel.offer(
                        Intent.TrackClick(
                            trackMediaInfo.track
                        )
                    )
                }
            }
        tracksAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT
        podcastTracksRecycler.adapter = tracksAdapter
        podcastTracksRecycler.setHasFixedSize(true)
    }

    private fun initTrackPositionHandler() {
        val layoutManager = podcastTracksRecycler.layoutManager as LinearLayoutManager
        val transition = Slide(Gravity.TOP).apply {
            addTarget(podcastTrackScrollButton)
            duration = resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        }

        fun switchTrackScrollButtonVisibility(isVisible: Boolean) {
            if (podcastTrackScrollButton.isVisible == isVisible) return
            TransitionManager.beginDelayedTransition(requireView() as ViewGroup, transition)
            podcastTrackScrollButton.isVisible = isVisible
        }

        val trackPositionScrollerHelper =
            TrackPositionScrollerHelper(
                this,
                { layoutManager.findFirstCompletelyVisibleItemPosition() to layoutManager.findLastCompletelyVisibleItemPosition() },
                { switchTrackScrollButtonVisibility(it) },
                { podcastTracksRecycler.smoothScrollToPosition(it) }
            )

        podcastTracksRecycler.tag = trackPositionScrollerHelper
        podcastTracksRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_DRAGGING) {
                    trackPositionScrollerHelper.wasScrolledByUser = true
                }
            }
        })

        podcastTrackScrollButton.setOnClickListener {
            trackPositionScrollerHelper.onScrollButtonClicked()
        }

    }

    private fun loadHeader(params: PodcastDetailsParams) {
        podcastDetailsHeaderImage.loadImage(
            params.cover,
            ImageLoaderParams(
                animate = ImageLoaderParams.Animation.CrossFade,
                scale = ImageLoaderParams.Scale.CenterCrop,
                transformations = listOf(
                    BlurTransformation.create(
                        requireContext(),
                        radius = 50f,
                        color = ColorUtils.setAlphaComponent(params.headerColor, 180)
                    ),
                    GranularRoundedCornersTransformation(
                        topLeft = 0f,
                        topRight = 0f,
                        bottomRight = resources.getDimensionPixelSize(R.dimen.podcastDetailsHeaderCornerSize)
                            .toFloat(),
                        bottomLeft = 0f
                    )
                )
            )
        )
    }

    private fun loadCover(params: PodcastDetailsParams, onLoaded: () -> Unit) {
        podcastDetailsCoverView.loadImage(
            params.cover,
            ImageLoaderParams(
                loaderCallbacks = arrayOf(doOnFinallyImageCallback(onLoaded)),
                transformations = listOf(CircleTransformation())
            )
        )
    }

}