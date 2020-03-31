package io.radio.presentation.player

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

        viewModel.scrubbingTimeFormattedFlow.subscribe {
            playerCurrentDurationView.text = it
        }

        viewModel.trackTimeLineFlow.subscribe {
            val data = it.data
            if (data == null) {
                playerSeekBar.isEnabled = false
                playerSeekBar.max = 0
                playerCurrentDurationView.text = ""
                playerTotalDurationView.text = ""
            } else {
                playerSeekBar.isEnabled = true
                playerSeekBar.max = data.totalDuration.toInt(DurationUnit.SECONDS)
                playerSeekBar.progress = data.currentPosition.toInt(DurationUnit.SECONDS)
                playerSeekBar.secondaryProgress =
                    data.bufferedPosition.toInt(DurationUnit.SECONDS)
                playerCurrentDurationView.text = data.currentDurationFormatted
                playerTotalDurationView.text = data.totalDurationFormatted
            }
        }

        viewModel.trackMediaStateFlow.subscribe {
            when (it.data) {
                TrackMediaState.Buffering,
                TrackMediaState.Ended,
                TrackMediaState.Play -> {
                    playerPlayButton.isEnabled = true
                    playerPlayButton.pause(true)
                }
                TrackMediaState.Pause -> {
                    playerPlayButton.isEnabled = true
                    playerPlayButton.play(true)
                }
                else -> {
                    playerPlayButton.isEnabled = false
                    playerPlayButton.play(true)
                }
            }
        }

        playerPlayButton.setOnClickListener { viewModel.onPlayClicked() }
        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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


}