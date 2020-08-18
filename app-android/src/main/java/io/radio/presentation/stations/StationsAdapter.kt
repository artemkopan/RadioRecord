package io.radio.presentation.stations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.radio.R
import io.radio.recycler.plugins.ClickItemAdapterEvent
import io.radio.recycler.plugins.ClickItemAdapterPlugin
import io.shared.imageloader.ImageLoaderParams
import io.shared.imageloader.loadImage
import io.shared.model.Station
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_station.*

class StationsAdapter(onClickItemAdapterEvent: ClickItemAdapterEvent<Station>) :
    ListAdapter<Station, StationsAdapter.StationHolder>(
        Diff
    ) {

    private val clickPlugin =
        ClickItemAdapterPlugin<Station>(onClickItemAdapterEvent, { getItem(it) })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationHolder {
        return StationHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_station, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StationHolder, position: Int) {
        holder.bind(getItem(position))
        clickPlugin.bindOnClickListener(holder, holder.itemView)
    }

    class StationHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: Station) {
            stationPreviewImage.loadImage(
                item.iconGray,
                ImageLoaderParams(
                    animate = ImageLoaderParams.Animation.CrossFade
                )
            )
            stationTitleView.text = item.title
        }

    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<Station>() {
            override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
                return oldItem == newItem
            }
        }
    }

}