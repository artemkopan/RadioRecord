package io.radio.presentation.player

import android.Manifest
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import io.radio.R
import io.radio.shared.base.Logger
import io.radio.shared.base.extensions.isPermissionGranted
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.base.fragment.popBack
import io.radio.shared.base.getOrThrow
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.CircleTransformation
import io.radio.shared.base.isEmpty
import io.radio.shared.base.viewmodel.koin.viewModels
import io.radio.shared.model.TrackMediaState
import io.radio.shared.presentation.player.PlayerViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlin.properties.Delegates
import kotlin.time.DurationUnit

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val viewModel by viewModels<PlayerViewModel>()

    private val permission = prepareCall(ActivityResultContracts.RequestPermission()) { granted ->
        visualizerState = visualizerState.copy(second = granted)
    }

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
                    animate = ImageLoaderParams.Animation.CrossFade,
                    transformations = listOf(CircleTransformation())
                )
            )
            playerTitleView.text = it.title
            playerSubTitleView.text = it.subTitle
        }

        viewModel.visualizationColorFlow.subscribe {
            playerAudioVisualizer.setColor(it)
        }

        initVisualizer()

        viewModel.trackTimeLine.subscribe {
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
                viewModel.onPositionChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }


    private var visualizerState by Delegates.observable(-1 to false) { _, old, new ->
        Logger.d("visualizerState() called with: old = $old, new = $new")
        if (old != new) {
            runCatching {
                playerAudioVisualizer.setPlayer(new.first)
            }
        }
    }

    private fun initVisualizer() {
        fun release() {
            runCatching { playerAudioVisualizer.release() }
        }

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                release()
            }
        })

        viewModel.playerMetaDataFlow.subscribe {
            if (it.isEmpty()) {
                release()
            } else {
                if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                    visualizerState = it.getOrThrow().sessionId to true
                } else {
                    visualizerState = visualizerState.copy(first = it.getOrThrow().sessionId)
                    permission.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }


}