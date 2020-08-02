@file:Suppress("NOTHING_TO_INLINE")

package io.radio.presentation.home

import androidx.fragment.app.Fragment

interface HomePagerContentScroller {

    fun onScrolled(fraction: Float)

}

inline fun Fragment.postScrolledFraction(fraction: Float) {
    (parentFragment as HomePagerContentScroller).onScrolled(fraction)
}
