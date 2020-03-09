package io.radio.shared

import io.radio.shared.common.Inject

interface SystemConfig {

    val isNetworkLogsEnabled: Boolean
}

class SystemConfigImpl @Inject constructor() : SystemConfig {

    override val isNetworkLogsEnabled: Boolean
        get() = true //fixme add implementation for different builds type

}