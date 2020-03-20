@file:Suppress("NOTHING_TO_INLINE")

package io.radio.shared.presentation.view

import android.annotation.SuppressLint
import android.view.animation.Interpolator
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import io.radio.shared.R
import kotlin.math.abs


open class ScrollOffsetListener(
    private val factor: Float = 1f,
    private val onFraction: (Float) -> Unit
) :
    RecyclerView.OnScrollListener(),
    NestedScrollView.OnScrollChangeListener,
    OnOffsetChangedListener {

    protected var interpolator: Interpolator = BASE_INTERPOLATOR

    @SuppressLint("RestrictedApi")
    override fun onScrollChange(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        calculateFraction(
            v.computeVerticalScrollOffset(),
            v.computeVerticalScrollRange(),
            v.computeVerticalScrollExtent()
        )
    }

    override fun onScrolled(v: RecyclerView, dx: Int, dy: Int) {
        calculateFraction(
            v.computeVerticalScrollOffset(),
            v.computeVerticalScrollRange(),
            v.computeVerticalScrollExtent()
        )
    }

    override fun onOffsetChanged(v: AppBarLayout, verticalOffset: Int) {
        calculateFraction(abs(verticalOffset), v.totalScrollRange, 0)
    }

    protected open fun calculateFraction(offset: Int, range: Int, extent: Int) {
        val f = offset / (range - extent).toFloat()
        fraction(interpolator.getInterpolation(f * factor))
    }

    protected fun fraction(f: Float) {
        onFraction(f)
    }

    companion object {
        private val BASE_INTERPOLATOR = FastOutSlowInInterpolator()
    }
}

inline fun RecyclerView.addScrollOffsetListener(
    listener: ScrollOffsetListener,
    addToTags: Boolean = true
): ScrollOffsetListener {
    addOnScrollListener(listener)
    updateScrollOffsetListener(listener)
    if (addToTags) {
        setTag(R.id.scroll_offset_lister, listener)
    }
    return listener
}

inline fun RecyclerView.updateScrollOffsetListener(
    listener: ScrollOffsetListener = getTag(R.id.scroll_offset_lister) as ScrollOffsetListener
) {
    listener.onScrolled(this, scrollX, scrollY)
}