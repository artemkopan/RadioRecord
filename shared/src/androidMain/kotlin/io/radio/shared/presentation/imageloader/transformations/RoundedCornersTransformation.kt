@file:Suppress("FunctionName")

package io.radio.shared.presentation.imageloader.transformations

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.radio.shared.presentation.imageloader.ImageTransformation

fun RoundedCornersTransformation(roundingRadius: Int): ImageTransformation {
    return object : ImageTransformation {
        override val bitmapTransformation: BitmapTransformation by lazy {
            RoundedCorners(roundingRadius)
        }
    }
}