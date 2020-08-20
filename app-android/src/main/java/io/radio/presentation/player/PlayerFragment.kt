package io.radio.presentation.player

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.popBack
import io.radio.base.showToast
import io.radio.di.binder.viewBinder
import io.radio.extensions.parseResourceString
import io.shared.imageloader.ImageLoaderParams
import io.shared.imageloader.loadImage
import io.shared.imageloader.transformations.CircleTransformation
import io.radio.binds.bindOnChangeListener
import io.radio.binds.bindOnClick
import io.radio.databinding.FragmentPlayerBinding
import io.radio.databinding.FragmentPodcastsBinding
import io.shared.presentation.player.PlayerView
import io.shared.presentation.player.PlayerView.*
import io.shared.presentation.player.PlayerViewBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class PlayerFragment : BaseFragment(R.layout.fragment_player), PlayerView {

    private val viewBinder by viewBinder<PlayerViewBinder>()
    private val binding: FragmentPlayerBinding by viewBinding { fragment ->
        FragmentPlayerBinding.bind(fragment.requireView())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialFadeThrough()
        exitTransition = returnTransition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playerToolbar.setNavigationOnClickListener { popBack() }
        binding.playerSubTitleView.movementMethod = ScrollingMovementMethod.getInstance()
        scope.attachBinder(viewBinder)
    }

    override val intents: Flow<Intent>
        get() = merge(
            binding.playerPlayButton.bindOnClick().map { Intent.PlayPause },
            binding.playerSkipPreviousButton.bindOnClick().map { Intent.PlayPrevious },
            binding.playerSkipNextButton.bindOnClick().map { Intent.PlayNext },
            binding.playerTimeBar.bindOnChangeListener().transform {
                if (it.fromUser) emit(
                    Intent.FindPosition(
                        it.progress,
                        it.isScrubbing
                    )
                )
            },
           binding.playerRewindAreaView.bindOnClick().map { Intent.SlipRewind },
           binding.playerForwardAreaView.bindOnClick().map { Intent.SlipForward }
        )

    override fun render(model: Model) {
        with(model) {
            binding.playerCoverImage.loadImage(
                cover, params = ImageLoaderParams(transformations = listOf(CircleTransformation()))
            )
            binding.playerTitleView.text = title
            binding.playerSubTitleView.text = subTitle

            binding.playerCurrentDurationView.text = currentDurationFormatted
            binding.playerTotalDurationView.text = totalDurationFormatted

            binding.playerSkipNextButton.isEnabled = isNextAvailable
            binding.playerSkipPreviousButton.isEnabled = isPreviousAvailable
            binding.playerRewindAreaView.isEnabled = isRewindAvailable
            binding.playerForwardAreaView.isEnabled = isFastForwardAvailable

            binding.playerPlayButton.isEnabled = !isLoading
            if (isPlaying) {
                binding.playerPlayButton.pause(true)
            } else {
                binding.playerPlayButton.play(true)
            }

            binding.playerTimeBar.isEnabled = isSeekingAvailable
            binding.playerTimeBar.progress = currentDuration
            binding.playerTimeBar.max = totalDuration

            model.slip?.let {
                when (it) {
                    is Model.Slip.Rewind -> {
                        showSlipView(it.timeFormatted, false)
                    }
                    is Model.Slip.Forward -> {
                        showSlipView(it.timeFormatted, true)
                    }
                }
            }
        }
    }

    override fun acceptEffect(effect: Effect) = with(effect) {
        when (this) {
            is Effect.Error -> showToast(parseResourceString(message))
        }
    }

    private fun showSlipView(timeOffset: String, isForward: Boolean) {
        fun showTime(viewToShow: View, viewToHide: View) {
            viewToShow.animate()
                .alpha(1f)
                .withEndAction {
                    viewToShow.animate().alpha(0f).setDuration(SEEK_DURATION).setStartDelay(400)
                        .start()
                }
                .setStartDelay(0L)
                .setDuration(SEEK_DURATION)
                .start()

            viewToHide.animate()
                .alpha(0f)
                .withEndAction { }
                .setStartDelay(0L)
                .setDuration(SEEK_DURATION)
                .start()
        }

        if (isForward) {
            binding.playerForwardTimeView.text = timeOffset
            showTime(binding.playerForwardTimeView, binding.playerRewindTimeView)
            (binding.playerForwardImage.drawable as Animatable).start()
        } else {
            binding.playerRewindTimeView.text = timeOffset
            showTime(binding.playerRewindTimeView, binding.playerForwardTimeView)
            (binding.playerRewindImage.drawable as Animatable).start()
        }
    }

    private companion object {
        const val SEEK_DURATION = 150L
    }

}

