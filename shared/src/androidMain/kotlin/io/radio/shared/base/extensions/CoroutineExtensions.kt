@file:JvmName("AndroidCoroutineExtensions")
package io.radio.shared.base.extensions

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.android.asCoroutineDispatcher

fun HandlerThread.asCoroutineDispatcher(name: String) =
    Handler(looper).asCoroutineDispatcher("ExoPlayer")