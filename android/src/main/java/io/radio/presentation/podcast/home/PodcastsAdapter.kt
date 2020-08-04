package io.radio.presentation.podcast.home

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.radio.R
import io.radio.shared.base.extensions.lazyNonSafety
import io.radio.shared.base.imageloader.CacheStrategy
import io.radio.shared.base.imageloader.ImageLoaderParams
import io.radio.shared.base.imageloader.loadImage
import io.radio.shared.base.imageloader.transformations.RoundedCornersTransformation
import io.radio.shared.base.recycler.ItemHolder
import io.radio.shared.base.recycler.plugins.ClickItemAdapterEvent
import io.radio.shared.base.recycler.plugins.ClickItemAdapterPlugin
import io.radio.shared.model.Podcast
import kotlinx.android.synthetic.main.item_station.*

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
        holder.stationPreviewImage.transitionName = "preview_$position"
        clickPlugin.bindOnClickListener(
            holder,
            holder.itemView,
            extras = holder.stationPreviewImage to holder.stationPreviewImage.transitionName
        )
    }

    inner class PodcastHolder(override val containerView: View) :
        ItemHolder<Podcast>(containerView) {

        override fun bind(item: Podcast, payloads: List<Any>) {
            stationPreviewImage.loadImage(
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
            stationTitleView.text = item.name
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