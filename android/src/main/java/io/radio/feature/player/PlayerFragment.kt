package io.radio.feature.player

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.CircleTransformation
import io.radio.shared.base.mvi.MaiViewInterceptor
import io.radio.shared.base.mvi.bindOnChangeListener
import io.radio.shared.base.mvi.bindOnClick
import io.radio.shared.base.viewmodel.koin.viewModel
import io.radio.shared.feature.player.PlayerAction
import io.radio.shared.feature.player.PlayerSideEffect
import io.radio.shared.feature.player.PlayerState
import io.radio.shared.feature.player.PlayerViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val viewModel by viewModel<PlayerViewModel>()

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

        scope.launch { viewModel.store.bind(mviViewHandler) }

    }

    private val mviViewHandler = MaiViewInterceptor(
        savedStateRegistry,
        merge(
            playerPlayButton.bindOnClick().map { PlayerAction.PlayPauseIntent },
            playerSkipPreviousButton.bindOnClick().map { PlayerAction.PlayPreviousIntent },
            playerSkipNextButton.bindOnClick().map { PlayerAction.PlayNextIntent },
            playerTimeBar.bindOnChangeListener().transform {
                if (it.fromUser) emit(
                    PlayerAction.FindPositionIntent(
                        it.progress,
                        it.isScrubbing
                    )
                )
            },
            playerRewindAreaView.bindOnClick().map { PlayerAction.SlipRewindIntent },
            playerForwardAreaView.bindOnClick().map { PlayerAction.SlipForwardIntent }
        ),
        ::render,
        ::sideEffect
    )

    private fun render(state: PlayerState) = with(state) {
        playerCoverImage.loadImage(
            logo, params = ImageLoaderParams(transformations = listOf(CircleTransformation()))
        )
        playerTitleView.text = title
        playerSubTitleView.text = subTitle

        playerCurrentDurationView.text = currentDurationFormatted
        playerTotalDurationView.text = totalDurationFormatted

        playerSkipNextButton.isEnabled = isNextAvailable
        playerSkipPreviousButton.isEnabled = isPreviousAvailable
        playerRewindAreaView.isEnabled = isSeekAvailable
        playerForwardAreaView.isEnabled = isSeekAvailable
    }

    private fun sideEffect(effect: PlayerSideEffect) {
        when (effect) {
            is PlayerSideEffect.Error -> {
                Snackbar.make(
                    requireView(),
                    effect.throwable.message.orEmpty(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            is PlayerSideEffect.SeekInScrubbing -> {
                playerCurrentDurationView.text = effect.formattedCurrentTime
            }
            is PlayerSideEffect.Slip.Rewind -> showSlipView(effect.timeFormatted, false)
            is PlayerSideEffect.Slip.Forward -> showSlipView(effect.timeFormatted, true)
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