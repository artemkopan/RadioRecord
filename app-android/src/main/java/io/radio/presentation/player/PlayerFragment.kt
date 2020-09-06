package io.radio.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.rawPressStartGestureFilter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.base.popBack
import io.radio.di.viewBinder
import io.radio.extensions.isLandscapeMode
import io.radio.ui.AppTheme
import io.radio.ui.ProvideDisplayInsets
import io.radio.ui.statusBarsPadding
import io.radio.ui.widgets.GlideImage
import io.shared.core.Logger
import io.shared.imageloader.defaultParams
import io.shared.imageloader.transformations.CircleTransformation
import io.shared.presentation.player.PlayerView.Intent
import io.shared.presentation.player.PlayerView.Model
import io.shared.presentation.player.PlayerViewBinder
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.seconds

class PlayerFragment : BaseFragment(R.layout.fragment_player) {

    private val viewBinder by viewBinder<PlayerViewBinder>()
    private val intentChannel = BroadcastChannel<Intent>(1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    ProvideDisplayInsets {
                        Scaffold(
                            topBar = { Toolbar() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Player()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Toolbar() {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = { popBack() }) {
                Icon(
                    asset = vectorResource(id = R.drawable.ic_left_arrow_accent)
                )
            }
        }
    }

    @Composable
    fun Player() {
        val model = viewBinder.modelFlow.collectAsState(initial = Model()).value

        launchInComposition {
            viewBinder.bindIntents(this, intentChannel.asFlow())
        }

        ConstraintLayout(Modifier.fillMaxSize()) {
            val (
                cover,
                title,
                description,
                play,
                skipPrevious,
                skipNext,
                timeBar,
                totalDuration,
                currentDuration,
                rewindArea,
                forwardArea,
                rewindImage,
                rewindTime,
                forwardImage,
                forwardTime,
            ) = createRefs()

            PlayerTrackInfo(model, cover, title, description, currentDuration)
            PlayerActionButtons(model, play, skipPrevious, skipNext)
            PlayerTimeController(
                model,
                timeBar,
                play,
                currentDuration,
                totalDuration,
                rewindArea
            )
        }
    }

    var sliderScrubbingPosition = 0f

    @Composable
    private fun ConstraintLayoutScope.PlayerTimeController(
        model: Model,
        timeBar: ConstrainedLayoutReference,
        play: ConstrainedLayoutReference,
        currentDuration: ConstrainedLayoutReference,
        totalDuration: ConstrainedLayoutReference,
        rewindArea: ConstrainedLayoutReference
    ) {
        fun Duration.asSeconds(): Float = toInt(DurationUnit.SECONDS).toFloat()
        fun Float.asSeconds(): Duration = toInt().seconds

        var fromUser by remember(false) { mutableStateOf(false) }
        var position by remember(0f) { mutableStateOf(0f) }

        Box(modifier = Modifier
            .height(12.dp)
            .padding(horizontal = 16.dp)
            .constrainAs(timeBar) {
                bottom.linkTo(play.top, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .rawPressStartGestureFilter(onPressStart = {
                Logger.d("pressStart", tag = "1234")
            })
        ) {
            Slider(
                value = model.currentDuration.inSeconds.toFloat(),
                onValueChange = { value ->
//                Logger.d("new value $value")
                    position = value
//                if (fromUser) {
//                    intentChannel.offer(Intent.FindPosition(value.asSeconds(), true))
//                }
                },
                onValueChangeEnd = {
//                Logger.d("sliderScrubbingPosition = $position")
//                fromUser = false
                    if (model.isSeekingAvailable) {
                        intentChannel.offer(Intent.FindPosition(position.asSeconds(), false))
                    }
                },
                activeTrackColor = if (model.isSeekingAvailable) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primary.copy(alpha = SliderConstants.InactiveTrackColorAlpha)
                },
                valueRange = 0f..model.totalDuration.asSeconds(),

                )
        }

        Text(text = model.currentDurationFormatted,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(start = 22.dp)
                .constrainAs(currentDuration) {
                    start.linkTo(timeBar.start)
                    bottom.linkTo(timeBar.top, margin = 4.dp)
                })

        Text(text = model.totalDurationFormatted,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(end = 22.dp)
                .constrainAs(totalDuration) {
                    end.linkTo(timeBar.end)
                    bottom.linkTo(timeBar.top, margin = 4.dp)
                })

        Box(modifier = Modifier.Companion.constrainAs(rewindArea) {
            width = Dimension.percent(.3f)
            height = Dimension.percent(0.5f)
            start.linkTo(parent.start)
            top.linkTo(parent.top)
        }) {

        }
    }

    @Composable
    private fun ConstraintLayoutScope.PlayerTrackInfo(
        model: Model,
        cover: ConstrainedLayoutReference,
        title: ConstrainedLayoutReference,
        description: ConstrainedLayoutReference,
        currentDuration: ConstrainedLayoutReference
    ) {
        GlideImage(
            model = model.cover,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .aspectRatio(1f)
                .constrainAs(cover) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = if (isLandscapeMode) 16.dp else 32.dp)
                    width = Dimension.percent(if (isLandscapeMode) 0.12f else 0.3f)
                },
            params = defaultParams.copy(transformations = listOf(CircleTransformation()))
        )

        Text(text = model.title,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .constrainAs(title) {
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent
                    centerHorizontallyTo(parent)
                    top.linkTo(cover.bottom)
                }
        )

        Text(text = model.subTitle,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                .verticalScroll(rememberScrollState())
                .constrainAs(description) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    centerHorizontallyTo(parent)
                    bottom.linkTo(currentDuration.top)
                    top.linkTo(title.bottom)
                }
        )
    }

    @Composable
    private fun ConstraintLayoutScope.PlayerActionButtons(
        model: Model,
        play: ConstrainedLayoutReference,
        skipPrevious: ConstrainedLayoutReference,
        skipNext: ConstrainedLayoutReference
    ) {
        PlayerButton(
            constraintModifier = Modifier
                .constrainAs(play) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                },
            iconRes = if (model.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            },
            isEnable = !model.isLoading,
            onClick = {
                intentChannel.offer(Intent.PlayPause)
            }
        )

        PlayerButton(
            constraintModifier = Modifier.Companion.constrainAs(skipPrevious) {
                end.linkTo(play.start, margin = 8.dp)
                bottom.linkTo(play.bottom)
            },
            iconRes = R.drawable.ic_skip_previous,
            isEnable = model.isPreviousAvailable,
            onClick = { intentChannel.offer(Intent.PlayPrevious) }
        )

        PlayerButton(
            constraintModifier = Modifier.Companion.constrainAs(skipNext) {
                start.linkTo(play.end, margin = 8.dp)
                bottom.linkTo(play.bottom)
            },
            iconRes = R.drawable.ic_skip_next,
            isEnable = model.isNextAvailable,
            onClick = { intentChannel.offer(Intent.PlayNext) }
        )
    }

    @Composable
    fun PlayerButton(
        constraintModifier: Modifier,
        @DrawableRes iconRes: Int,
        isEnable: Boolean,
        onClick: () -> Unit
    ) {
        val playerButtonSize = 62.dp
        Box(
            padding = 8.dp,
            gravity = ContentGravity.Center,
            modifier = Modifier
                .preferredSize(playerButtonSize)
                .clickable(
                    onClick = onClick,
                    enabled = isEnable,
                    indication = RippleIndication(radius = playerButtonSize / 2)
                )
                .then(constraintModifier)
        )
        {
            Image(
                asset = vectorResource(id = iconRes),
                colorFilter = ColorFilter.tint(contentColor()),
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Preview
    @Composable
    fun Player_Preview() {
        Text(text = "test")
    }

//    override fun render(model: Model) {
//        with(model) {
//            binding.playerCoverImage.loadImage(
//                cover, params = ImageLoaderParams(transformations = listOf(CircleTransformation()))
//            )
//            binding.playerTitleView.text = title
//            binding.playerSubTitleView.text = subTitle
//
//            binding.playerCurrentDurationView.text = currentDurationFormatted
//            binding.playerTotalDurationView.text = totalDurationFormatted
//
//            binding.playerSkipNextButton.isEnabled = isNextAvailable
//            binding.playerSkipPreviousButton.isEnabled = isPreviousAvailable
//            binding.playerRewindAreaView.isEnabled = isRewindAvailable
//            binding.playerForwardAreaView.isEnabled = isFastForwardAvailable
//
//            binding.playerPlayButton.isEnabled = !isLoading
//            if (isPlaying) {
//                binding.playerPlayButton.pause(true)
//            } else {
//                binding.playerPlayButton.play(true)
//            }
//
//            binding.playerTimeBar.isEnabled = isSeekingAvailable
//            binding.playerTimeBar.progress = currentDuration
//            binding.playerTimeBar.max = totalDuration
//
//            model.slip?.let {
//                when (it) {
//                    is Model.Slip.Rewind -> {
//                        showSlipView(it.timeFormatted, false)
//                    }
//                    is Model.Slip.Forward -> {
//                        showSlipView(it.timeFormatted, true)
//                    }
//                }
//            }
//        }
//    }

//    override fun acceptEffect(effect: Effect) = with(effect) {
//        when (this) {
//            is Effect.Error -> showToast(parseResourceString(message))
//        }
//    }

//    private fun showSlipView(timeOffset: String, isForward: Boolean) {
//        fun showTime(viewToShow: View, viewToHide: View) {
//            viewToShow.animate()
//                .alpha(1f)
//                .withEndAction {
//                    viewToShow.animate().alpha(0f).setDuration(SEEK_DURATION).setStartDelay(400)
//                        .start()
//                }
//                .setStartDelay(0L)
//                .setDuration(SEEK_DURATION)
//                .start()
//
//            viewToHide.animate()
//                .alpha(0f)
//                .withEndAction { }
//                .setStartDelay(0L)
//                .setDuration(SEEK_DURATION)
//                .start()
//        }
//
//        if (isForward) {
//            binding.playerForwardTimeView.text = timeOffset
//            showTime(binding.playerForwardTimeView, binding.playerRewindTimeView)
//            (binding.playerForwardImage.drawable as Animatable).start()
//        } else {
//            binding.playerRewindTimeView.text = timeOffset
//            showTime(binding.playerRewindTimeView, binding.playerForwardTimeView)
//            (binding.playerRewindImage.drawable as Animatable).start()
//        }
//    }

    private companion object {
        const val SEEK_DURATION = 150L
    }

}

