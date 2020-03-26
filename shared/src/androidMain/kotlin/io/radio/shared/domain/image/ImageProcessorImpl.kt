package io.radio.shared.domain.image

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Size
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette.Swatch
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.imageloader.loadImageDrawable
import kotlinx.coroutines.withContext

class ImageProcessorImpl(private val context: Context) : ImageProcessor {


    override suspend fun <Source : Any> getImage(source: Source, width: Int, height: Int): Image =
        withContext(IoDispatcher) {
            val drawable = context.loadImageDrawable(source, Size(width, height)) as BitmapDrawable
            return@withContext drawable.bitmap
        }

    override suspend fun generatePalette(image: Image): Palette = withContext(IoDispatcher) {
        Palette.from(image).generate()
    }

    override suspend fun getLightness(palette: Palette): ImageLightness =
        withContext(IoDispatcher) {
            val mostPopulous =
                palette.getMostPopulousSwatch() ?: return@withContext ImageLightness.Unknown
            if (ColorUtils.calculateLuminance(mostPopulous.rgb) < 0.5) {
                ImageLightness.Dark
            } else {
                ImageLightness.Light
            }
        }

    override suspend fun getDominantColor(palette: Palette, defaultColor: Int): Int =
        withContext(IoDispatcher) {
            palette.getDominantColor(defaultColor)
        }

    override suspend fun getDarkerColor(@ColorInt color: Int): Int = withContext(IoDispatcher) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = hsv[1] + 0.1f
        hsv[2] = hsv[2] - 0.1f
        Color.HSVToColor(hsv)
    }


    private fun Palette.getMostPopulousSwatch(): Swatch? {
        var mostPopulous: Swatch? = null
        for (swatch in swatches) {
            if (mostPopulous == null || swatch.population > mostPopulous.population) {
                mostPopulous = swatch
            }
        }
        return mostPopulous
    }

}