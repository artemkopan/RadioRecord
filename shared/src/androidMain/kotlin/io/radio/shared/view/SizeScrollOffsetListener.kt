package io.radio.shared.view

import androidx.annotation.FloatRange


class SizeScrollOffsetListener(
    @FloatRange(from = 0.0)
    private val spaceToOffset: Float,
    private val factor: Float = 1f,
    onFraction: (Float) -> Unit
) : ScrollOffsetListener(factor, onFraction) {

    override fun calculateFraction(offset: Int, range: Int, extent: Int) {
        if (spaceToOffset < 0) {
            return
        }
        val f = offset / spaceToOffset
        fraction(interpolator.getInterpolation(f * factor))
    }

}
