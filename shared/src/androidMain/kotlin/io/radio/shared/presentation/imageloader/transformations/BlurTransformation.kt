package io.radio.shared.presentation.imageloader.transformations

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.ColorInt
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import io.radio.shared.presentation.imageloader.ImageTransformation
import java.security.MessageDigest


class BlurTransformation private constructor(
    private val context: Context,
    private val radius: Float = DEFAULT_RADIUS,
    private val sampling: Float = DEFAULT_SAMPLING,
    @ColorInt
    private val color: Int = Color.TRANSPARENT
) : BitmapTransformation(), ImageTransformation {

    override val bitmapTransformation: BitmapTransformation
        get() = this

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + sampling + color).toByteArray(Key.CHARSET))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val radius: Float
        val sampling: Float

        if (this.radius > MAX_RADIUS) {
            sampling = this.radius / 25.0f
            radius = MAX_RADIUS
        } else {
            sampling = this.sampling
            radius = this.radius
        }

        val needScaled = sampling == DEFAULT_SAMPLING
        val originWidth = toTransform.width
        val originHeight = toTransform.height
        val width: Int
        val height: Int
        if (needScaled) {
            width = originWidth
            height = originHeight
        } else {
            width = (originWidth / sampling).toInt()
            height = (originHeight / sampling).toInt()
        }

        //find a re-use bitmap
        val bitmap: Bitmap = pool[width, height, Bitmap.Config.ARGB_8888]

        val canvas = Canvas(bitmap)
        if (sampling != DEFAULT_SAMPLING) {
            canvas.scale(1 / sampling, 1 / sampling)
        }
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        blur(context, bitmap, radius)

        return if (needScaled) {
            bitmap
        } else {
            val scaled = Bitmap.createScaledBitmap(bitmap, originWidth, originHeight, true)
            bitmap.recycle()
            scaled
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlurTransformation

        if (radius != other.radius) return false
        if (sampling != other.sampling) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius.hashCode()
        result = 31 * result + sampling.hashCode()
        result = 31 * result + color
        return result
    }

    private fun blur(context: Context, bitmap: Bitmap, radius: Float) {
        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(context)
            rs.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(
                rs,
                bitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            output = Allocation.createTyped(rs, input.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
                setInput(input)
                setRadius(radius)
                forEach(output)
            }
            output.copyTo(bitmap)
        } finally {
            rs?.destroy()
            input?.destroy()
            output?.destroy()
            blur?.destroy()
        }
    }

    companion object {
        private const val VERSION = 1
        private const val ID =
            "io.radio.shared.presentation.imageloader.transformations.BlurTransformation.$VERSION"

        private const val DEFAULT_RADIUS = 25.0f
        private const val MAX_RADIUS = 25.0f
        private const val DEFAULT_SAMPLING = 1.0f


        fun create(
            context: Context,
            radius: Float = DEFAULT_RADIUS,
            sampling: Float = DEFAULT_SAMPLING,
            @ColorInt
            color: Int = Color.TRANSPARENT
        ): ImageTransformation {
            return BlurTransformation(context, radius, sampling, color)
        }
    }

}