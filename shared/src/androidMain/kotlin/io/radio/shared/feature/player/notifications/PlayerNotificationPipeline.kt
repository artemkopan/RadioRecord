package io.radio.shared.feature.player.notifications

import kotlinx.coroutines.flow.Flow

interface PlayerNotificationPipeline {

    fun notificationFlow(): Flow<PlayerNotification>

}