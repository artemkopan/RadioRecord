package io.radio.presentation.podcast.details

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.presentation.routePlayer
import io.radio.presentation.track.TracksAdapter
import io.radio.shared.base.State
import io.radio.shared.base.extensions.isLandscapeMode
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.doOnFinallyImageCallback
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.BlurTransformation
import io.radio.shared.base.imageloader.transformations.CircleTransformation
import io.radio.shared.base.imageloader.transformations.GranularRoundedCornersTransformation
import io.radio.shared.base.imageloader.transformations.RoundedCornersTransformation
import io.radio.shared.base.recycler.RecyclerViewStateController
import io.radio.shared.base.viewmodel.koin.viewModels
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams
import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PodcastDetailsFragment : BaseFragment(R.layout.fragment_podcast_details) {

    private val viewModel by viewModels<PodcastDetailsViewModel>()
    private val stateController = RecyclerViewStateController(this) {
        podcastTracksRecycler?.layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
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
        enterTransition = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, false)
        returnTransition = MaterialFadeThrough.create(requireContext())
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
            loadHeader(params, it)
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

        viewModel.openPlayerEventFlow.subscribe { it.performContentIfNotHandled { routePlayer() } }

        viewModel.podcastDetailsFlow
            .onEach {
                when (it) {
                    is State.Success -> {

                    }
                    is State.Fail -> {

                    }
                    State.Loading -> {

                    }
                }
            }
            .launchIn(viewScope)
    }

    private fun initTracksAdapter() {
        val tracksAdapter = TracksAdapter { _, viewId, _, trackMediaInfo, _ ->
            when (viewId.id) {
                R.id.playButton -> viewModel.onPlayClick(trackMediaInfo)
                R.id.trackContainerLayout -> viewModel.onTrackClick(trackMediaInfo)
            }
        }
        tracksAdapter.setHasStableIds(true)
        podcastTracksRecycler.adapter = tracksAdapter
        podcastTracksRecycler.setHasFixedSize(true)

        viewModel.trackItemsFlow
            .subscribe {
                tracksAdapter.submitList(it) { stateController.restoreState() }
            }
    }

    private fun loadHeader(
        params: PodcastDetailsParams,
        it: View
    ) {
        podcastDetailsHeaderImage.loadImage(
            params.cover,
            ImageLoaderParams(
                animate = ImageLoaderParams.Animation.CrossFade,
                scale = ImageLoaderParams.Scale.CenterCrop,
                transformations = listOf(
                    BlurTransformation.create(
                        requireContext(),
                        radius = 50f,
                        color = ColorUtils.setAlphaComponent(params.headerColor, 120)
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
                transformations = listOf(
                    if (isLandscapeMode) {
                        CircleTransformation()
                    } else {
                        RoundedCornersTransformation(resources.getDimensionPixelSize(R.dimen.itemCornerRadius))
                    }
                )
            )
        )
    }


    private companion object {
        const val ANIM_DURATION = 500L
    }

}