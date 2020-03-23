
package io.radio.shared.imageloader.transformations

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.radio.shared.imageloader.ImageTransformation

class RoundedCornersTransformation(roundingRadius: Int):
    ImageTransformation {

    override val bitmapTransformation: BitmapTransformation by lazy {
        RoundedCorners(roundingRadius)
    }

}