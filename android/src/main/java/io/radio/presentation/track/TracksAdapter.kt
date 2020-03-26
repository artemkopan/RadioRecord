package io.radio.presentation.track

import android.graphics.drawable.Animatable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import io.radio.R
import io.radio.shared.base.Logger
import io.radio.shared.base.recycler.ItemHolder
import io.radio.shared.base.recycler.inflate
import io.radio.shared.base.recycler.plugins.ClickItemAdapterEvent
import io.radio.shared.base.recycler.plugins.ClickItemAdapterPlugin
import io.radio.shared.model.TrackMediaInfo
import io.radio.shared.model.TrackMediaState
import io.radio.shared.model.isPlayOrPause
import kotlinx.android.synthetic.main.item_track.*

class TracksAdapter(clickItemAdapterEvent: ClickItemAdapterEvent<TrackMediaInfo>) :
    ListAdapter<TrackMediaInfo, TracksAdapter.TrackViewHolder>(Diff) {

    private val clickPlugin =
        ClickItemAdapterPlugin<TrackMediaInfo>(
            clickItemAdapterEvent
        ) { getItem(it) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(inflate(parent, R.layout.item_track))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(getItem(position), payloads)
    }

    override fun onViewDetachedFromWindow(holder: TrackViewHolder) {
        TransitionManager.endTransitions(holder.containerView as ViewGroup)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).track.id.toLong()
    }

    inner class TrackViewHolder(override val containerView: View) :
        ItemHolder<TrackMediaInfo>(containerView) {

        override fun bind(item: TrackMediaInfo, payloads: List<Any>) {
            clickPlugin.bindOnClickListener(this, playButton)
            clickPlugin.bindOnClickListener(this, itemView)
            clickPlugin.bindOnClickListener(this, warningIcon)


            titleView.text = item.track.title
            subTitleView.text = item.track.subTitle
            subTitleView.isVisible = item.track.subTitle.isNotEmpty()
            timeView.text = item.playTimeFormatted

            fun animatePlayPauseButton() = payloads.contains(PlayPausePayload)

            when (item.state) {
                TrackMediaState.Buffering,
                TrackMediaState.Preparing -> {
                    switchProgress(true)
                    playButton.play(false)
                    switchError(false, payloads)
                }
                TrackMediaState.None -> {
                    switchProgress(false)
                    playButton.play(false)
                    switchError(false, payloads)
                }
                TrackMediaState.Play -> {
                    switchProgress(false)
                    playButton.pause(animatePlayPauseButton())
                    switchError(false, payloads)
                }
                TrackMediaState.Pause -> {
                    switchProgress(false)
                    playButton.play(animatePlayPauseButton())
                    switchError(false, payloads)
                }
                is TrackMediaState.Error -> {
                    switchProgress(false)
                    playButton.play(false)
                    switchError(true, payloads)
                }
                else -> throw NotImplementedError()
            }
        }

        private fun switchProgress(isProgress: Boolean) {
            (progressBar.drawable as Animatable).run {
                if (isProgress) start() else stop()
            }
            progressBar.isVisible = isProgress
            playButton.isInvisible = isProgress
            playButton.isEnabled = !isProgress
            Logger.d("${progressBar.parent} $progressBar ${progressBar.isVisible} ${progressBar.alpha}")
        }

        private fun switchError(isShow: Boolean, payloads: List<Any>) {
            warningIcon.isVisible = isShow
            if (payloads.contains(ErrorPayload)) {
                TransitionManager.beginDelayedTransition(
                    containerView as ViewGroup,
                    createErrorTransition()
                )
            }
        }

        private fun createErrorTransition(): TransitionSet {
            return TransitionSet().apply {

                addTransition(Fade().apply {
                    addTarget(warningIcon)
                })
                addTransition(ChangeBounds().apply {
                    addTarget(timeView)
                    addTarget(warningIcon)
                })
            }
        }

    }

    companion object {

        private val PlayPausePayload = Any()
        private val PreparingPayload = Any()
        private val ErrorPayload = Any()

        private val Diff = object : DiffUtil.ItemCallback<TrackMediaInfo>() {
            override fun areItemsTheSame(
                oldItem: TrackMediaInfo,
                newItem: TrackMediaInfo
            ): Boolean {
                return oldItem.track.id == newItem.track.id
            }

            override fun areContentsTheSame(
                oldItem: TrackMediaInfo,
                newItem: TrackMediaInfo
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: TrackMediaInfo, newItem: TrackMediaInfo): Any? {
                return when {
                    oldItem.state != newItem.state && newItem.state.isPlayOrPause() -> PlayPausePayload
                    newItem.state == TrackMediaState.Preparing -> PreparingPayload
                    oldItem.state is TrackMediaState.Error && newItem.state !is TrackMediaState.Error -> ErrorPayload
                    oldItem.state !is TrackMediaState.Error && newItem.state is TrackMediaState.Error -> ErrorPayload
                    else -> Unit
                }
            }
        }

    }


}