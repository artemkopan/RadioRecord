package io.shared.image

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Size
import androidx.core.graphics.ColorUtils
import io.shared.core.IoDispatcher
import io.shared.imageloader.loadImageDrawable
import kotlinx.coroutines.withContext

typealias AndroidColor = android.graphics.Color

actual class ImageProcessor(private val context: Context) {

    actual suspend fun <Source : Any> getImage(source: Source, width: Int, height: Int): Image =
        withContext(IoDispatcher) {
            val drawable = context.loadImageDrawable(source, Size(width, height)) as BitmapDrawable
            return@withContext drawable.bitmap
        }

    actual suspend fun generatePalette(image: Image): Palette = withContext(IoDispatcher) {
        Palette.from(image).generate()
    }

    actual suspend fun getLightness(palette: Palette): ImageLightness =
        withContext(IoDispatcher) {
            val mostPopulous =
                palette.getMostPopulousSwatch() ?: return@withContext ImageLightness.Unknown
            if (ColorUtils.calculateLuminance(mostPopulous.rgb) < 0.5) {
                ImageLightness.Dark
            } else {
                ImageLightness.Light
            }
        }

    actual suspend fun getDominantColor(
        palette: Palette,
        defaultColor: Color
    ): Color =
        withContext(IoDispatcher) {
            Color(palette.getDominantColor(defaultColor.value))
        }

    actual suspend fun getDarkerColor(color: Color): Color =
        withContext(IoDispatcher) {
            val hsv = FloatArray(3)
            AndroidColor.colorToHSV(color.value, hsv)
            hsv[1] = hsv[1] + 0.1f
            hsv[2] = hsv[2] - 0.1f
            Color(AndroidColor.HSVToColor(hsv))
        }


    private fun Palette.getMostPopulousSwatch(): androidx.palette.graphics.Palette.Swatch? {
        var mostPopulous: androidx.palette.graphics.Palette.Swatch? = null
        for (swatch in swatches) {
            if (mostPopulous == null || swatch.population > mostPopulous.population) {
                mostPopulous = swatch
            }
        }
        return mostPopulous
    }
}