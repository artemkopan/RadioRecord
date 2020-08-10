@file:Suppress("MemberVisibilityCanBePrivate", "NOTHING_TO_INLINE")

package io.radio.recycler.plugins

import android.view.View
import androidx.recyclerview.widget.RecyclerView

typealias ClickItemAdapterEvent<T> = (View, ViewId, Position, T, Extras) -> Unit

class ClickItemAdapterPlugin<T : Any>(
    private val onClickEvent: ClickItemAdapterEvent<T>,
    private val getItem: (Int) -> T
) {

    fun bindOnClickListener(holder: RecyclerView.ViewHolder, view: View, extras: Any = Unit) {
        bindOnClickListener({ holder.adapterPosition }, view, extras)
    }

    fun bindOnClickListener(adapterPosition: () -> Int, view: View, extras: Any = Unit) {
        view.setOnClickListener {
            val position = adapterPosition()
            onClickEvent.invoke(
                it,
                ViewId(it.id),
                Position(position),
                getItem(position),
                Extras(extras)
            )
        }
    }

}


inline class ViewId(val id: Int)
inline class Position(val value: Int)
inline class Extras(val value: Any) {

    @Suppress("UNCHECKED_CAST")
    inline fun asTransitionExtraPair() = value as Pair<View, String>

}