package io.radio.shared.domain.configs

interface SystemConfig {

    val isNetworkLogsEnabled: Boolean
}

class SystemConfigImpl : SystemConfig {

    override val isNetworkLogsEnabled: Boolean
        get() = true //fixme add implementation for different builds type

}