package io.radio.data.player

import android.app.Notification
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.radio.shared.base.Logger
import io.radio.shared.domain.player.notifications.PlayerNotification
import io.radio.shared.domain.player.notifications.PlayerNotificationPipeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class NotificationMediaListener(
    private val scope: CoroutineScope
) : PlayerNotificationManager.NotificationListener,
    PlayerNotificationPipeline {

    private val channel = BroadcastChannel<PlayerNotification>(1)

    override fun notificationFlow(): Flow<PlayerNotification> = channel.asFlow()

    override fun onNotificationStarted(notificationId: Int, notification: Notification) {
        Logger.d("onNotificationStarted() called with: notificationId = $notificationId, notification = $notification")
        scope.launch { channel.send(PlayerNotification.Show(notificationId, notification)) }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        Logger.d("onNotificationCancelled() called with: notificationId = $notificationId, dismissedByUser = $dismissedByUser")
        scope.launch { channel.send(PlayerNotification.Cancel(notificationId, dismissedByUser)) }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        Logger.d("onNotificationPosted() called with: notificationId = $notificationId, notification = $notification, ongoing = $ongoing")
        scope.launch {
            channel.send(
                PlayerNotification.Posted(
                    notificationId,
                    notification,
                    ongoing
                )
            )
        }
    }
}