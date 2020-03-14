package io.radio.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.radio.R
import io.radio.shared.model.RadioPodcast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_station.*

class PodcastAdapter : ListAdapter<RadioPodcast, PodcastAdapter.PodcastHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHolder {
        return PodcastHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_podcast, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PodcastHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: RadioPodcast) {
            Glide.with(podcastPreviewImage)
                .load(item.cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(podcastPreviewImage)
            podcastTitleView.text = item.name
        }

    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<RadioPodcast>() {
            override fun areItemsTheSame(oldItem: RadioPodcast, newItem: RadioPodcast): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RadioPodcast, newItem: RadioPodcast): Boolean {
                return oldItem == newItem
            }
        }
    }

}