package io.radio.ui.widgets

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.graphics.drawscope.drawCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.ContextAmbient
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.shared.imageloader.*

@Composable
fun GlideImage(
    model: Any,
    modifier: Modifier = Modifier,
    params: ImageLoaderParams = defaultParams
) {
    WithConstraints(modifier) {

        val image = remember { mutableStateOf<ImageAsset?>(null) }
        val drawable = remember { mutableStateOf<Drawable?>(null) }
        val context = ContextAmbient.current.applicationContext
        val target = object : CustomTarget<Drawable>() {

            override fun onLoadCleared(placeholder: Drawable?) {
                image.value = null
                drawable.value = placeholder
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                drawable.value = errorDrawable
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                FrameManager.ensureStarted()
                image.value = (resource as BitmapDrawable).bitmap.asImageAsset()
            }

        }

        onCommit(model) {
            val width =
                if (constraints.maxWidth > 0 && constraints.maxWidth < Int.MAX_VALUE) {
                    constraints.maxWidth
                } else {
                    SIZE_ORIGINAL
                }

            val height =
                if (constraints.maxHeight > 0 && constraints.maxHeight < Int.MAX_VALUE) {
                    constraints.maxHeight
                } else {
                    SIZE_ORIGINAL
                }

            val updatedParams = if (params.resize == null) {
                params.copy(resize = Resize(width, height))
            } else {
                params
            }
            context.loadImage(model, updatedParams).into(target)
        }
        onDispose {
            image.value = null
            drawable.value = null
            Glide.with(context).clear(target)
        }

        val theImage = image.value
        val theDrawable = drawable.value
        if (theImage != null) {
            Image(asset = theImage)
        } else if (theDrawable != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCanvas { canvas, _ -> theDrawable.draw(canvas.nativeCanvas) }
            }
        }
    }
}