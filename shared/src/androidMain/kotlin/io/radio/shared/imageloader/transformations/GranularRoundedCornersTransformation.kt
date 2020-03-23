package io.radio.shared.imageloader.transformations

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import io.radio.shared.imageloader.ImageTransformation


class GranularRoundedCornersTransformation(
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float
) : ImageTransformation {

    override val bitmapTransformation: BitmapTransformation by lazy {
        GranularRoundedCorners(topLeft, topRight, bottomRight, bottomLeft)
    }

}