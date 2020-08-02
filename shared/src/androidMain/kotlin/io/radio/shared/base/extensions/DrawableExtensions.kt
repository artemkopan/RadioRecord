package io.radio.shared.base.extensions

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable?.getBitmap(): Bitmap? {
    return (this as? BitmapDrawable)?.bitmap
}