package io.radio.shared.presentation.imageloader.transformations

import android.R.attr.angle
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import io.radio.shared.common.getDarkerColor
import io.radio.shared.presentation.imageloader.ImageTransformation
import java.security.MessageDigest


class ShadowTransformation private constructor(
    private val elevation: Float,
    private val shadowParams: ShadowParams,
    private val shadowMargins: Margins,
    private val roundCornerRadius: Float,
    @ColorInt
    private val color: Int = AUTO_DETECT,
    @ColorInt
    private val defaultColor: Int = Color.BLACK
) : BitmapTransformation(), ImageTransformation {

    private var shadowColor: Int = defaultColor

    override val bitmapTransformation: BitmapTransformation
        get() = this

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(
            (ID + elevation + shadowParams + shadowMargins + roundCornerRadius + color + defaultColor)
                .toByteArray(Key.CHARSET)
        )
    }

    override fun transform(
        pool: BitmapPool,
        source: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        shadowColor = getDarkerColor(generateShadowColor(source))

        val bitmap = pool[source.width, source.height, Bitmap.Config.ARGB_8888]

        val canvas = Canvas(bitmap)

        val elevationH = elevation / 2f

        val shadowRectF = RectF(
            elevationH + shadowMargins.left,
            shadowMargins.top,
            outWidth.toFloat() - elevationH - shadowMargins.right,
            outHeight.toFloat() - elevation - shadowMargins.bottom
        )

        val bitmapRectF = RectF(
            elevationH,
            0f,
            outWidth.toFloat() - elevationH,
            outHeight - elevation
        )


        val shadowPaint = Paint()
        shadowPaint.color = Color.WHITE
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.isAntiAlias = true

        with(shadowParams) {
            shadowPaint.setShadowLayer(radius, dx, dy, shadowColor)
        }

        canvas.drawRoundRect(shadowRectF, roundCornerRadius, roundCornerRadius, shadowPaint)
        canvas.drawBitmap(source, null, bitmapRectF, null)

        return bitmap
    }

    private fun generateShadowColor(bitmap: Bitmap): Int {
        return if (color == AUTO_DETECT) {
            Palette.from(bitmap).generate().getDominantColor(defaultColor)
        } else {
            color
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is ShadowTransformation) {
            return shadowParams == other.shadowParams &&
                    elevation == other.elevation &&
                    shadowColor == other.shadowColor
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return Util.hashCode(
            ID.hashCode(),
            Util.hashCode(
                shadowParams.radius,
                Util.hashCode(
                    elevation,
                    Util.hashCode(
                        angle,
                        Util.hashCode(shadowColor)
                    )
                )
            )
        )
    }


    data class ShadowParams(
        val radius: Float,
        val dx: Float = 0f,
        val dy: Float = 0f
    )

    data class Margins(
        val left: Float = 0f,
        val top: Float = 0f,
        val right: Float = 0f,
        val bottom: Float = 0f
    )

    companion object {

        const val AUTO_DETECT = -1

        private const val ID = "io.radio.shared.presentation.imageloader.transformations.Shadow"

        fun create(
            elevation: Float,
            shadowParams: ShadowParams,
            shadowMargins: Margins,
            roundCornerRadius: Float,
            @ColorInt
            color: Int = AUTO_DETECT,
            @ColorInt
            defaultColor: Int = Color.BLACK
        ): ImageTransformation {
            return ShadowTransformation(
                elevation, shadowParams, shadowMargins, roundCornerRadius, color, defaultColor
            )
        }

    }

}