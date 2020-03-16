package io.radio.presentation.station

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.radio.R
import io.radio.shared.model.RadioStation
import io.radio.shared.presentation.imageloader.ImageLoaderParams
import io.radio.shared.presentation.imageloader.loadImage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_station.*

class StationsAdapter : ListAdapter<RadioStation, StationsAdapter.StationHolder>(
    Diff
) {

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
            stationPreviewImage.loadImage(
                item.icon,
                ImageLoaderParams(
                    animate = ImageLoaderParams.Animation.CrossFade
                )
            )
            stationTitleView.text = item.title
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