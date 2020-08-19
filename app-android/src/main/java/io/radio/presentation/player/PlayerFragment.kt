package io.radio.presentation.player

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.popBack
import io.radio.base.showToast
import io.radio.di.binder.viewBinder
import io.radio.extensions.parseResourceString
import io.shared.core.Logger
import io.shared.imageloader.ImageLoaderParams
import io.shared.imageloader.loadImage
import io.shared.imageloader.transformations.CircleTransformation
import io.shared.mvi.bindOnChangeListener
import io.shared.mvi.bindOnClick
import io.shared.presentation.player.PlayerView
import io.shared.presentation.player.PlayerView.*
import io.shared.presentation.player.PlayerViewBinder
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class PlayerFragment : BaseFragment(R.layout.fragment_player), PlayerView {

    private val viewBinder by viewBinder<PlayerViewBinder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialFadeThrough()
        exitTransition = returnTransition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerToolbar.setNavigationOnClickListener { popBack() }
        playerSubTitleView.movementMethod = ScrollingMovementMethod.getInstance()
        scope.attachBinder(viewBinder)
    }

    override val intents: Flow<Intent>
        get() = merge(
            playerPlayButton.bindOnClick().map { Intent.PlayPause },
            playerSkipPreviousButton.bindOnClick().map { Intent.PlayPrevious },
            playerSkipNextButton.bindOnClick().map { Intent.PlayNext },
            playerTimeBar.bindOnChangeListener().transform {
                if (it.fromUser) emit(
                    Intent.FindPosition(
                        it.progress,
                        it.isScrubbing
                    )
                )
            },
            playerRewindAreaView.bindOnClick().map { Intent.SlipRewind },
            playerForwardAreaView.bindOnClick().map { Intent.SlipForward }
        )

    override fun render(model: Model) {
        with(model) {
            playerCoverImage.loadImage(
                cover, params = ImageLoaderParams(transformations = listOf(CircleTransformation()))
            )
            playerTitleView.text = title
            playerSubTitleView.text = subTitle

            playerCurrentDurationView.text = currentDurationFormatted
            playerTotalDurationView.text = totalDurationFormatted

            playerSkipNextButton.isEnabled = isNextAvailable
            playerSkipPreviousButton.isEnabled = isPreviousAvailable
            playerRewindAreaView.isEnabled = isRewindAvailable
            playerForwardAreaView.isEnabled = isFastForwardAvailable

            playerPlayButton.isEnabled = !isLoading
            if (isPlaying) {
                playerPlayButton.pause(true)
            } else {
                playerPlayButton.play(true)
            }

            playerTimeBar.isEnabled = isSeekingAvailable
            playerTimeBar.progress = currentDuration
            playerTimeBar.max = totalDuration

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
            playerForwardTimeView.text = timeOffset
            showTime(playerForwardTimeView, playerRewindTimeView)
            (playerForwardImage.drawable as Animatable).start()
        } else {
            playerRewindTimeView.text = timeOffset
            showTime(playerRewindTimeView, playerForwardTimeView)
            (playerRewindImage.drawable as Animatable).start()
        }
    }

    private companion object {
        const val SEEK_DURATION = 150L
    }

}

