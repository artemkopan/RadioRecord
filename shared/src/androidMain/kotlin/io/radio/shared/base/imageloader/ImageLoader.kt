@file:Suppress("EXPERIMENTAL_FEATURE_WARNING", "unused")

package io.radio.shared.base.imageloader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.radio.shared.base.Logger
import io.radio.shared.base.imageloader.ImageLoaderParams.Scale.*
import java.io.File

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    source: Any,
    params: ImageLoaderParams = defaultParams
) {
    context.loadImage(source, params).into(this)
}

fun Context.loadImageDrawable(
    source: Any,
    size: Size,
    params: ImageLoaderParams = defaultParams
): Drawable {
    return loadImage(source, params).submit(size.width, size.height).get()
}

@SuppressLint("CheckResult")
fun Context.loadImage(
    source: Any,
    params: ImageLoaderParams = defaultParams
): RequestBuilder<Drawable> {

    val creator = Glide.with(this)
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


    val transformations: List<BitmapTransformation>

    if (params.transformations.isEmpty()) {
        when (params.scale) {
            CenterCrop -> creator.centerCrop()
            CenterInside -> creator.centerInside()
            None -> {
                /*no-op*/
            }
        }
        transformations = emptyList()
    } else {
        transformations = mutableListOf<BitmapTransformation>()
            .apply {
                when (params.scale) {
                    CenterCrop -> add(com.bumptech.glide.load.resource.bitmap.CenterCrop())
                    CenterInside -> add(com.bumptech.glide.load.resource.bitmap.CenterInside())
                    None -> {
                        //no-op
                    }
                }
                addAll(params.transformations.map { it.bitmapTransformation })
            }
    }

    creator.diskCacheStrategy(
        when (params.cacheStrategy) {
            CacheStrategy.All -> DiskCacheStrategy.ALL
            CacheStrategy.Origin -> DiskCacheStrategy.DATA
            CacheStrategy.Decoded -> DiskCacheStrategy.RESOURCE
            CacheStrategy.Disable -> DiskCacheStrategy.NONE
            CacheStrategy.Default -> DiskCacheStrategy.AUTOMATIC
        }
    )

    when {
        transformations.size == 1 -> {
            creator.transform(transformations.first())
        }
        transformations.size > 1 -> {
            creator.transform(MultiTransformation(transformations))
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

    return creator
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
    val cacheStrategy: CacheStrategy = CacheStrategy.Default,
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

enum class CacheStrategy {
    Origin, //cache origin image
    Decoded, //cache decoded image
    All, //cache decoded and origin image
    Disable, //disable cache
    Default //default cache which provides by cache library
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
