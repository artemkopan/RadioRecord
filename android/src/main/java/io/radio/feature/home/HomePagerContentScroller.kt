@file:Suppress("NOTHING_TO_INLINE")

package io.radio.feature.home

import androidx.fragment.app.Fragment

interface HomePagerContentScroller {

    fun onScrolled(fraction: Float)

}

inline fun Fragment.postScrolledFraction(fraction: Float) {
    (parentFragment as HomePagerContentScroller).onScrolled(fraction)
}
