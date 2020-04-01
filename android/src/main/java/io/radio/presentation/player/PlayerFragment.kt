package io.radio.presentation.player

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.SeekBar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.CircleTransformation
import io.radio.shared.base.viewmodel.koin.viewModels
import io.radio.shared.model.TrackMediaState
import io.radio.shared.presentation.player.PlayerViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlin.time.DurationUnit

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val viewModel by viewModels<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, true)
        returnTransition = MaterialFadeThrough.create(requireContext())
        exitTransition = returnTransition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerToolbar.setNavigationOnClickListener { popBack() }
        playerSubTitleView.movementMethod = ScrollingMovementMethod.getInstance()

        viewModel.trackFlow.subscribe {
            playerCoverImage.loadImage(
                it.cover.data?.img.orEmpty(), params = ImageLoaderParams(
                    transformations = listOf(CircleTransformation())
                )
            )
            playerTitleView.text = it.title
            playerSubTitleView.text = it.subTitle
        }

        viewModel.subTitleFlow.subscribe {
            playerSubTitleView.text = it
        }

        viewModel.scrubbingTimeFormattedFlow.subscribe {
            playerCurrentDurationView.text = it
        }


        setupTrackStates()
        setupTimeLineBar()
        setupSeek()
        playerPlayButton.setOnClickListener { viewModel.onPlayClicked() }
    }

    private fun setupSeek() {
        playerRewindAreaView.setOnClickListener {
            viewModel.rewind()
        }
        playerForwardAreaView.setOnClickListener {
            viewModel.forward()
        }

        viewModel.availableSeekFlow.subscribe {
            playerRewindAreaView.isEnabled = it
            playerForwardAreaView.isEnabled = it
        }

        fun showTime(viewToShow: View, viewToHide: View) {
            viewToShow.animate().alpha(1f).setDuration(SEEK_DURATION).start()
            viewToHide.animate().alpha(0f).setDuration(SEEK_DURATION).start()
        }

        viewModel.seekResultFlow.subscribe {
            when (val result = it.data) {
                is PlayerViewModel.SeekResult.Forward -> {
                    playerForwardTimeView.text = result.timeOffset
                    showTime(playerForwardTimeView, playerRewindTimeView)
                    (playerForwardImage.drawable as Animatable).start()
                }
                is PlayerViewModel.SeekResult.Rewind -> {
                    playerRewindTimeView.text = result.timeOffset
                    showTime(playerRewindTimeView, playerForwardTimeView)
                    (playerRewindImage.drawable as Animatable).start()
                }
                else -> {
                    playerForwardTimeView.animate().alpha(0f).setDuration(SEEK_DURATION).start()
                    playerRewindTimeView.animate().alpha(0f).setDuration(SEEK_DURATION).start()
                }
            }
        }
    }

    private fun setupTimeLineBar() {
        viewModel.trackTimeLineFlow.subscribe {
            val data = it.data
            if (data == null) {
                playerTimeBar.isEnabled = false
                playerTimeBar.max = 0
                playerCurrentDurationView.text = ""
                playerTotalDurationView.text = ""
            } else {
                playerTimeBar.isEnabled = true
                playerTimeBar.max = data.totalDuration.toInt(DurationUnit.SECONDS)
                playerTimeBar.progress = data.currentPosition.toInt(DurationUnit.SECONDS)
                playerTimeBar.secondaryProgress =
                    data.bufferedPosition.toInt(DurationUnit.SECONDS)
                playerCurrentDurationView.text = data.currentDurationFormatted
                playerTotalDurationView.text = data.totalDurationFormatted
            }
        }
        playerTimeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) return
                viewModel.onPositionChanged(progress, true)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.onPositionChanged(seekBar.progress, false)
            }
        })
    }

    private fun setupTrackStates() {
        var previousState: TrackMediaState? = null

        fun animateButton(newState: TrackMediaState?): Boolean {
            return when {
                previousState == TrackMediaState.Buffering && newState == TrackMediaState.Play -> false
                else -> true
            }
        }

        viewModel.trackMediaStateFlow.subscribe {
            when (val state = it.data) {
                TrackMediaState.Preparing -> {
                    playerPlayButton.isEnabled = false
                    playerPlayButton.play(animateButton(state))
                }
                TrackMediaState.Buffering,
                TrackMediaState.Play -> {
                    playerPlayButton.isEnabled = true
                    playerPlayButton.pause(animateButton(state))
                }
                TrackMediaState.Pause -> {
                    playerPlayButton.isEnabled = true
                    playerPlayButton.play(animateButton(state))
                }
                else -> {
                    playerPlayButton.isEnabled = true
                    playerPlayButton.play(animateButton(state))
                }
            }
            previousState = it.data
        }
    }


    private companion object {
        const val SEEK_DURATION = 150L
    }

}