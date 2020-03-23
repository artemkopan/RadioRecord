package io.radio.presentation.podcast.details

import android.os.Bundle
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.circularreveal.CircularRevealCompat
import io.radio.R
import io.radio.shared.base.Logger
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.viewmodel.koin.getStateViewModel
import io.radio.shared.imageloader.ImageLoaderParams
import io.radio.shared.imageloader.Resize
import io.radio.shared.imageloader.loadImage
import io.radio.shared.imageloader.transformations.BlurTransformation
import io.radio.shared.imageloader.transformations.GranularRoundedCornersTransformation
import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModel
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import org.koin.android.ext.android.getKoin

class PodcastDetailsFragment : BaseFragment(R.layout.fragment_podcast_details) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

        val stateViewModel =
            getKoin().getStateViewModel<PodcastDetailsViewModel>(this, bundle = requireArguments())
        Logger.d(stateViewModel.toString())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = PodcastDetailsFragmentArgs.fromBundle(requireArguments()).params

        podcastDetailsToolbar.navigationIcon?.setTint(params.toolbarColor)
        podcastDetailsToolbar.setNavigationOnClickListener { popBack() }
        podcastDetailsHeader.setCardBackgroundColor(params.headerColor)


        podcastDetailsHeader.doOnPreDraw {

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
                            color = ColorUtils.setAlphaComponent(params.headerColor, 50)
                        ),
                        GranularRoundedCornersTransformation(
                            topLeft = 0f,
                            topRight = 0f,
                            bottomRight = resources.getDimensionPixelSize(R.dimen.podcastDetailsCornerSize)
                                .toFloat(),
                            bottomLeft = 0f
                        )
                    )
                )
            )

            startPostponedEnterTransition()
        }


    }


}