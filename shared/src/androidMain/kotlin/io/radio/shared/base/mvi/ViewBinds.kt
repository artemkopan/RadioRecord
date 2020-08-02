package io.radio.shared.base.mvi

import android.view.View
import android.widget.SeekBar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


fun View.bindOnClick() = callbackFlow {
    setOnClickListener {
        offer(Unit)
    }
    awaitClose { setOnClickListener(null) }
}

fun Array<View>.bindOnClick() = callbackFlow {
    val clickListener = View.OnClickListener {
        offer(ViewId(it.id))
    }
    forEach { it.setOnClickListener(clickListener) }
    awaitClose { forEach { it.setOnClickListener(null) } }
}

fun SeekBar.bindOnChangeListener() = callbackFlow {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            offer(SeekBarProgress(progress, fromUser, fromUser))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            offer(SeekBarProgress(progress = progress, fromUser = true, isScrubbing = true))
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            offer(SeekBarProgress(progress = progress, fromUser = true, isScrubbing = false))
        }
    })
    awaitClose { setOnSeekBarChangeListener(null) }
}


data class SeekBarProgress(val progress: Int, val fromUser: Boolean, val isScrubbing: Boolean)

inline class ViewId(val id: Int)