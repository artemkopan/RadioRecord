package io.radio.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

abstract class ItemHolder<T>(containerView: View) : BaseHolder(containerView) {

    fun bind(item: T) = bind(item, emptyList())

    abstract fun bind(item: T, payloads: List<Any>)

}