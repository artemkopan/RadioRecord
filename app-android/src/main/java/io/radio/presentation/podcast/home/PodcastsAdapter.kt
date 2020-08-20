package io.radio.presentation.podcast.home

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.radio.R
import io.radio.databinding.ItemPodcastBinding
import io.radio.recycler.ItemHolder
import io.radio.recycler.plugins.ClickItemAdapterEvent
import io.radio.recycler.plugins.ClickItemAdapterPlugin
import io.shared.core.extensions.lazyNonSafety
import io.shared.imageloader.CacheStrategy
import io.shared.imageloader.ImageLoaderParams
import io.shared.imageloader.loadImage
import io.shared.imageloader.transformations.RoundedCornersTransformation
import io.shared.model.Podcast

class PodcastsAdapter(
    private val resources: Resources,
    onClickEvent: ClickItemAdapterEvent<Podcast>
) : ListAdapter<Podcast, PodcastsAdapter.PodcastHolder>(
    Diff
) {

    private val roundingCorner by lazyNonSafety {
        resources.getDimensionPixelSize(R.dimen.itemCornerRadius)
    }

    private val clickPlugin =
        ClickItemAdapterPlugin<Podcast>(
            onClickEvent
        ) { getItem(it) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHolder {
        return PodcastHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_podcast, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.stationPreviewImage.transitionName = "preview_$position"
        clickPlugin.bindOnClickListener(
            holder,
            holder.itemView,
            extras = holder.binding.stationPreviewImage to holder.binding.stationPreviewImage.transitionName
        )
    }

    inner class PodcastHolder(containerView: View) :
        ItemHolder<Podcast>(containerView) {

        val binding = ItemPodcastBinding.bind(containerView)

        override fun bind(item: Podcast, payloads: List<Any>) {
            binding.stationPreviewImage.loadImage(
                item.cover,
                ImageLoaderParams(
                    cacheStrategy = CacheStrategy.All,
                    scale = ImageLoaderParams.Scale.CenterCrop,
                    transformations = listOf(
                        RoundedCornersTransformation(
                            roundingRadius = roundingCorner
                        )
                    )
                )
            )
            binding.stationTitleView.text = item.name
        }

    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<Podcast>() {
            override fun areItemsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Podcast, newItem: Podcast): Boolean {
                return oldItem == newItem
            }
        }
    }

}