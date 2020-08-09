package io.shared.store.player

import kotlinx.coroutines.flow.Flow

interface PlayerNotificationPipeline {

    fun notificationFlow(): Flow<PlayerNotification>

}