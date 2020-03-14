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
import io.radio.shared.model.RadioStation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_station.*

class StationsAdapter : ListAdapter<RadioStation, StationsAdapter.StationHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        return StationHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_station, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StationHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: RadioStation) {
            Glide.with(podcastPreviewImage)
                .load(item.icon)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(podcastPreviewImage)
            podcastTitleView.text = item.title
        }

    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<RadioStation>() {
            override fun areItemsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
                return oldItem == newItem
            }
        }
    }

}