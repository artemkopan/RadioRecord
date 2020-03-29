package io.radio.shared.base.imageloader.transformations

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.radio.shared.base.imageloader.ImageTransformation

class CircleTransformation : ImageTransformation {
    override val bitmapTransformation: BitmapTransformation
        get() = CircleCrop()
}