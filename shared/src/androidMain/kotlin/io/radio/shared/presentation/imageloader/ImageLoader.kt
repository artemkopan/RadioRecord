@file:Suppress("EXPERIMENTAL_FEATURE_WARNING", "unused")

package io.radio.shared.presentation.imageloader

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.radio.shared.common.Logger
import io.radio.shared.presentation.imageloader.ImageLoaderParams.Scale.*
import java.io.File


@SuppressLint("CheckResult")
fun ImageView.loadImage(
    source: Any,
    params: ImageLoaderParams = defaultParams
) {

    val creator = Glide.with(context)
        .run {
            when (source) {
                is File -> load(source)
                is Uri -> load(source)
                is String -> load(source)
                is Int -> load(source)
                else -> throw IllegalArgumentException("Not supported source")
            }
        }

    when (params.animate) {
        ImageLoaderParams.Animation.None -> creator.dontAnimate()
        ImageLoaderParams.Animation.CrossFade -> creator.transition(DrawableTransitionOptions.withCrossFade())
    }

    when (params.errorHolder) {
        is PlaceHolder.Drawable -> creator.error(params.errorHolder.drawable)
        is PlaceHolder.Res -> creator.error(params.errorHolder.resourcesId)
    }
    when (params.placeHolder) {
        is PlaceHolder.Drawable -> creator.placeholder(params.placeHolder.drawable)
        is PlaceHolder.Res -> creator.placeholder(params.placeHolder.resourcesId)
    }

    if (params.resize != null) {
        creator.override(params.resize.width, params.resize.height)
    }

    when (params.scale) {
        CenterCrop -> creator.centerCrop()
        CenterInside -> creator.centerInside()
        None -> {
            /*no-op*/
        }
    }

    when {
        params.transformations.size == 1 -> {
            creator.transform(params.transformations.first().bitmapTransformation)
        }
        params.transformations.size > 1 -> {
            creator.transform(MultiTransformation(params.transformations.map { it.bitmapTransformation }))
        }
    }

    val callbackWrapper: RequestListener<Drawable>? =
        params.loaderCallbacks.takeIf { it.isNotEmpty() }?.let { callbacks ->
            object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Logger.w(e)
                    callbacks.forEach { it.onError(); it.onFinally() }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    callbacks.forEach { it.onSuccess(); it.onFinally() }
                    return false
                }

            }
        }

    if (params.loadFromCacheOnly) {
        creator.onlyRetrieveFromCache(true)
    }
    callbackWrapper?.let { creator.addListener(it) }
    creator.into(this)
}

val defaultParams = ImageLoaderParams()

class ImageLoaderParams(
    val animate: Animation = Animation.None,
    val scale: Scale = CenterCrop,
    val errorHolder: PlaceHolder? = null,
    val placeHolder: PlaceHolder? = null,
    val resize: Resize? = null,
    val transformations: List<ImageTransformation> = emptyList(),
    val loadFromCacheOnly: Boolean = false,
    val loaderCallbacks: Array<ImageLoaderCallback> = emptyArray()
) {

    enum class Scale {
        CenterCrop,
        CenterInside,
        None
    }

    sealed class Animation {

        object None : Animation()
        object CrossFade : Animation()

        //use sealed class for adding custom animations
    }

}

data class Resize(val width: Int, val height: Int)

sealed class PlaceHolder {
    class Drawable(val drawable: android.graphics.drawable.Drawable) : PlaceHolder()
    class Res(val resourcesId: Int) : PlaceHolder()
}

interface ImageTransformation {

    val bitmapTransformation: BitmapTransformation

}

interface ImageLoaderCallback {
    fun onError() {}
    fun onSuccess() {}
    fun onFinally() {}
}

inline fun doOnFinallyImageCallback(crossinline onFinally: () -> Unit): ImageLoaderCallback {
    return object : ImageLoaderCallback {
        override fun onFinally() {
            onFinally.invoke()
        }
    }
}
