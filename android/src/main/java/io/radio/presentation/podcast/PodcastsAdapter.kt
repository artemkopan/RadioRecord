package io.radio.presentation.podcast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.radio.R
import io.radio.shared.model.RadioPodcast
import io.radio.shared.presentation.imageloader.ImageLoaderParams
import io.radio.shared.presentation.imageloader.loadImage
import io.radio.shared.presentation.imageloader.transformations.RoundedCornersTransformation
import io.radio.shared.presentation.imageloader.transformations.ShadowTransformation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_station.*

class PodcastsAdapter(private val onClickEvent: (RadioPodcast) -> Unit) :
    ListAdapter<RadioPodcast, PodcastsAdapter.PodcastHolder>(
        Diff
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHolder {
        return PodcastHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_podcast, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PodcastHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onClickEvent(getItem(holder.adapterPosition)) }
    }

    class PodcastHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: RadioPodcast) {
            stationPreviewImage.loadImage(
                item.cover,
                ImageLoaderParams(
                    scale = ImageLoaderParams.Scale.CenterCrop,
                    animate = ImageLoaderParams.Animation.CrossFade,
                    transformations = listOf(
                        RoundedCornersTransformation(roundingRadius = 20),
                        ShadowTransformation.create(
                            elevation = 40f,
                            shadowParams = ShadowTransformation.ShadowParams(20f, dy = 10f),
                            shadowMargins = ShadowTransformation.Margins(left = 20f, right = 20f),
                            roundCornerRadius = 20f
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