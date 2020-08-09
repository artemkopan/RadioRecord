package io.radio.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer

//don't generate a caching map in the base holder
@ContainerOptions(cache = CacheImplementation.NO_CACHE)
open class BaseHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer

@ContainerOptions(cache = CacheImplementation.NO_CACHE)
abstract class ItemHolder<T>(override val containerView: View) : BaseHolder(containerView) {

    fun bind(item: T) = bind(item, emptyList())

    abstract fun bind(item: T, payloads: List<Any>)

}