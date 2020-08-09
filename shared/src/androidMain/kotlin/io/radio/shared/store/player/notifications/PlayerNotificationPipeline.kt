package io.shared.store.player.notifications

import kotlinx.coroutines.flow.Flow

interface PlayerNotificationPipeline {

    fun notificationFlow(): Flow<PlayerNotification>

}