package io.radio.shared.domain.player.notifications

import kotlinx.coroutines.flow.Flow

interface PlayerNotificationPipeline {

    fun notificationFlow(): Flow<PlayerNotification>

}