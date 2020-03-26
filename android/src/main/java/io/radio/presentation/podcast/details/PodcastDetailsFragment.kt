package io.radio.presentation.podcast.details

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.transition.MaterialContainerTransform
import io.radio.R
import io.radio.presentation.track.TracksAdapter
import io.radio.shared.base.State
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.FragmentTransitionsController
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.Resize
import io.radio.shared.base.imageloader.doOnFinallyImageCallback
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.BlurTransformation
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
    private val transitionsController = FragmentTransitionsController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            scrimColor = Color.TRANSPARENT
        }
        transitionsController.registerSharedEnterElementTransition()
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (names == null || sharedElements == null) return
                sharedElements[names.first()] = podcastDetailsCoverView
            }
        })
        postponeEnterTransition()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = PodcastDetailsFragmentArgs.fromBundle(requireArguments()).params

        podcastDetailsToolbar.navigationIcon?.setTint(params.toolbarColor)
        podcastDetailsToolbar.setNavigationOnClickListener { popBack() }
        podcastDetailsHeader.setCardBackgroundColor(params.headerColor)

        podcastDetailsHeader.doOnPreDraw {
            startAnimation(it)
            loadHeader(params, it)
            loadCover(params) {
                startPostponedEnterTransition()
            }
        }

        podcastDetailsTitle.setTextColor(params.toolbarColor)
        podcastDetailsTitle.text = params.name

        transitionsController.awaitTransitionAnimationComplete {
            initTracksAdapter()
        }

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
        val stateController = RecyclerViewStateController(this, podcastTracksRecycler)

        viewModel.trackItemsFlow
            .onEach { tracksAdapter.submitList(it) { stateController.restoreState() } }
            .launchIn(viewScope)
    }

    private fun startAnimation(it: View) {
        val finalRadius = it.width.coerceAtLeast(it.height) * 1.2f

        CircularRevealCompat.createCircularReveal(
            podcastDetailsHeader,
            0f,
            0f,
            0f,
            finalRadius
        )
            .apply {
                interpolator = FastOutSlowInInterpolator()
            }
            .setDuration(500L)
            .start()
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
                resize = Resize(
                    it.width,
                    it.height
                ),
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
                    RoundedCornersTransformation(
                        resources.getDimensionPixelSize(R.dimen.itemCornerRadius)
                    )
                )
            )
        )

    }

}