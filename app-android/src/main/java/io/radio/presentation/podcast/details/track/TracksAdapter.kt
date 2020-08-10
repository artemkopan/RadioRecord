package io.radio.presentation.podcast.details.track

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
import io.radio.recycler.ItemHolder
import io.radio.recycler.inflate
import io.radio.recycler.plugins.ClickItemAdapterEvent
import io.radio.recycler.plugins.ClickItemAdapterPlugin
import io.radio.shared.model.TrackPlaybackStateItem
import io.shared.store.player.PlaybackState
import kotlinx.android.synthetic.main.item_track.*

class TracksAdapter(clickItemAdapterEvent: ClickItemAdapterEvent<TrackPlaybackStateItem>) :
    ListAdapter<TrackPlaybackStateItem, TracksAdapter.TrackViewHolder>(Diff) {

    private val clickPlugin =
        ClickItemAdapterPlugin<TrackPlaybackStateItem>(clickItemAdapterEvent) { getItem(it) }

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
        ItemHolder<TrackPlaybackStateItem>(containerView) {

        override fun bind(item: TrackPlaybackStateItem, payloads: List<Any>) {
            clickPlugin.bindOnClickListener(this, playButton)
            clickPlugin.bindOnClickListener(this, itemView)
            clickPlugin.bindOnClickListener(this, warningIcon)


            titleView.text = item.track.title
            subTitleView.text = item.track.subTitle
            subTitleView.isVisible = item.track.subTitle.isNotEmpty()
            timeView.text = item.durationFormatted

            fun animatePlayPauseButton() = payloads.contains(PlayPausePayload)

            when (item.state) {
                PlaybackState.Buffering -> {
                    switchProgress(true)
                    playButton.play(false)
                    switchError(false, payloads)
                }
                PlaybackState.Idle -> {
                    switchProgress(false)
                    playButton.play(false)
                    switchError(false, payloads)
                }
                PlaybackState.Play -> {
                    switchProgress(false)
                    playButton.pause(animatePlayPauseButton())
                    switchError(false, payloads)
                }
                PlaybackState.Ended,
                PlaybackState.Pause -> {
                    switchProgress(false)
                    playButton.play(animatePlayPauseButton())
                    switchError(false, payloads)
                }
                is PlaybackState.Error -> {
                    switchProgress(false)
                    playButton.play(false)
                    switchError(true, payloads)
                }
                else -> throw NotImplementedError("Not implemented state: ${item.state}")
            }
        }

        private fun switchProgress(isProgress: Boolean) {
            (progressBar.drawable as Animatable).run {
                if (isProgress) start() else stop()
            }
            progressBar.isVisible = isProgress
            playButton.isInvisible = isProgress
            playButton.isEnabled = !isProgress
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

        private val Diff = object : DiffUtil.ItemCallback<TrackPlaybackStateItem>() {
            override fun areItemsTheSame(
                oldItem: TrackPlaybackStateItem,
                newItem: TrackPlaybackStateItem
            ): Boolean {
                return oldItem.track.id == newItem.track.id
            }

            override fun areContentsTheSame(
                oldItem: TrackPlaybackStateItem,
                newItem: TrackPlaybackStateItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(
                oldItem: TrackPlaybackStateItem,
                newItem: TrackPlaybackStateItem
            ): Any? {
                return when {
                    oldItem.state != newItem.state && newItem.state.isPlayOrPause() -> PlayPausePayload
                    newItem.state == PlaybackState.Buffering -> PreparingPayload
                    oldItem.state is PlaybackState.Error && newItem.state !is PlaybackState.Error -> ErrorPayload
                    oldItem.state !is PlaybackState.Error && newItem.state is PlaybackState.Error -> ErrorPayload
                    else -> Unit
                }
            }
        }

    }


}