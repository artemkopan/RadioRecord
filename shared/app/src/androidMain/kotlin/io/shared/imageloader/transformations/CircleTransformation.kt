package io.shared.imageloader.transformations

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.shared.imageloader.ImageTransformation

class CircleTransformation : ImageTransformation {
    override val bitmapTransformation: BitmapTransformation
        get() = CircleCrop()
}