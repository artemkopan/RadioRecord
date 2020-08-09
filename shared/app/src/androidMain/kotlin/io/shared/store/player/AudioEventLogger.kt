package io.shared.store.player

import com.google.android.exoplayer2.util.EventLogger
import io.shared.core.Logger

class AudioEventLogger(private val tag: String) : EventLogger(null, tag) {

    override fun logd(msg: String) {
        Logger.d(msg, tag = tag)
    }

    override fun loge(msg: String) {
        Logger.e(msg, tag = tag)
    }
}