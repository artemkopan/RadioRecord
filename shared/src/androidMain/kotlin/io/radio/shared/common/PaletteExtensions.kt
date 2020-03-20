package io.radio.shared.common

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.Nullable
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch


const val IS_LIGHT = 0
const val IS_DARK = 1
const val LIGHTNESS_UNKNOWN = 2


@Lightness
fun Palette.isDark(): Int {
    val mostPopulous: Swatch = getMostPopulousSwatch() ?: return LIGHTNESS_UNKNOWN
    return if (isDark(mostPopulous.rgb)) IS_DARK else IS_LIGHT
}

@Nullable
fun Palette.getMostPopulousSwatch(): Swatch? {
    var mostPopulous: Swatch? = null
    for (swatch in swatches) {
        if (mostPopulous == null || swatch.population > mostPopulous.population) {
            mostPopulous = swatch
        }
    }
    return mostPopulous
}

fun getDarkerColor(@ColorInt color: Int): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[1] = hsv[1] + 0.1f
    hsv[2] = hsv[2] - 0.1f
    return Color.HSVToColor(hsv)
}

/**
 * Check if a color is dark (convert to XYZ & check Y component)
 */
private fun isDark(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(IS_LIGHT, IS_DARK, LIGHTNESS_UNKNOWN)
annotation class Lightness