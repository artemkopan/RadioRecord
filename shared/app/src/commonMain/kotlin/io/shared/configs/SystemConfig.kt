package io.shared.configs

interface SystemConfig {

    val isNetworkLogsEnabled: Boolean
}

class SystemConfigImpl : SystemConfig {

    override val isNetworkLogsEnabled: Boolean
        get() = true //fixme add implementation for different builds type

}