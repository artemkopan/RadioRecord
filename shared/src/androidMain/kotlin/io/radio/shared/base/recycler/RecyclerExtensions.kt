package io.radio.shared.base.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


@Suppress("unused")
fun RecyclerView.Adapter<*>.inflate(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
}