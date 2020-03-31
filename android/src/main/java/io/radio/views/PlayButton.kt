package io.radio.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import io.radio.R
import io.radio.shared.base.extensions.lazyNonSafety

class PlayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val pause by lazyNonSafety {
        ContextCompat.getDrawable(context, R.drawable.ic_pause)!!
    }
    private val pauseToPlayAnim by lazyNonSafety {
        AnimatedVectorDrawableCompat.create(context, R.drawable.avd_pause_to_play)!!
    }
    private val play by lazyNonSafety {
        ContextCompat.getDrawable(context, R.drawable.ic_play)!!
    }
    private val playToPauseAnim by lazyNonSafety {
        AnimatedVectorDrawableCompat.create(context, R.drawable.avd_play_to_pause)!!
    }

    private var isPlay: Boolean? = null

    init {
        setImageDrawable(play)
    }

    fun switch(play: Boolean, animate: Boolean) {
        if (play) {
            pause(animate)
        } else {
            play(animate)
        }
    }

    fun play(animate: Boolean) {
        if (isPlay == true) return
        isPlay = true
        morph(if (animate) pauseToPlayAnim else play)
    }

    fun pause(animate: Boolean) {
        if (isPlay == false) return
        isPlay = false
        morph(if (animate) playToPauseAnim else pause)
    }

    private fun morph(drawable: Drawable) {
        setImageDrawable(drawable)
        if (drawable is AnimatedVectorDrawableCompat) {
            drawable.start()
        }
    }


}