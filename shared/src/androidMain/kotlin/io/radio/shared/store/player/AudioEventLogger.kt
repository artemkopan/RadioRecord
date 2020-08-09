package io.shared.store.player

import com.google.android.exoplayer2.util.EventLogger
import io.radio.shared.base.Logger

class AudioEventLogger(private val tag: String) : EventLogger(null, tag) {

    override fun logd(msg: String) {
        Logger.d(tag, msg)
    }

    override fun loge(msg: String, tr: Throwable?) {
        Logger.e(tag, msg, tr)
    }
}