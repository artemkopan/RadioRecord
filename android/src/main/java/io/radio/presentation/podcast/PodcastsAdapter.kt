package io.radio.presentation.podcast

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
import io.radio.shared.base.imageloader.transformations.ShadowTransformation
import io.radio.shared.base.recycler.ItemHolder
import io.radio.shared.base.recycler.plugins.ClickItemAdapterEvent
import io.radio.shared.base.recycler.plugins.ClickItemAdapterPlugin
import io.radio.shared.model.RadioPodcast
import kotlinx.android.synthetic.main.item_station.*

class PodcastsAdapter(
    private val resources: Resources,
    onClickEvent: ClickItemAdapterEvent<RadioPodcast>
) : ListAdapter<RadioPodcast, PodcastsAdapter.PodcastHolder>(Diff) {

    private val roundingCorner by lazyNonSafety {
        resources.getDimensionPixelSize(R.dimen.itemCornerRadius)
    }

    private val clickPlugin =
        ClickItemAdapterPlugin<RadioPodcast>(
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

    inner class PodcastHolder(override val containerView: View) : ItemHolder<RadioPodcast>(containerView) {

        override fun bind(item: RadioPodcast, payloads: List<Any>) {
            stationPreviewImage.loadImage(
                item.cover,
                ImageLoaderParams(
                    cacheStrategy = CacheStrategy.All,
                    scale = ImageLoaderParams.Scale.CenterCrop,
                    transformations = listOf(
                        RoundedCornersTransformation(
                            roundingRadius = roundingCorner
                        ),
                        ShadowTransformation.create(
                            elevation = 40f,
                            shadowParams = ShadowTransformation.ShadowParams(
                                roundingCorner.toFloat(),
                                dy = 10f
                            ),
                            shadowMargins = ShadowTransformation.Margins(
                                left = 20f,
                                right = 20f
                            ),
                            roundCornerRadius = roundingCorner.toFloat()
                        )
                    )
                )
            )
            stationTitleView.text = item.name
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