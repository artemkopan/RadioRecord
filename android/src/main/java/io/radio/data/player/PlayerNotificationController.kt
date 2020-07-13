package io.radio.data.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.collection.LruCache
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.radio.R
import io.radio.presentation.createPlayerPendingIntent
import io.radio.shared.base.IoDispatcher
import io.radio.shared.base.Logger
import io.radio.shared.base.extensions.CoroutineExceptionHandler
import io.radio.shared.base.extensions.JobRunner
import io.radio.shared.base.extensions.findBitmap
import io.radio.shared.base.imageloader.loadImageDrawable
import io.radio.shared.domain.player.notifications.PlayerNotification
import io.radio.shared.domain.player.notifications.PlayerNotificationPipeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class PlayerNotificationController(
    private val context: Context,
    private val scope: CoroutineScope
) : PlayerNotificationManager.MediaDescriptionAdapter,
    PlayerNotificationManager.NotificationListener,
    PlayerNotificationPipeline {

    private val channel = BroadcastChannel<PlayerNotification>(1)

    private val jobRunner = JobRunner()
    private val imageSize = Size(200, 200)
    private var lruCache = LruCache<String, Bitmap>(1)

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return context.createPlayerPendingIntent()
    }

    override fun getCurrentContentText(player: Player): String? {
        return player.currentTrack()?.subTitle
    }

    override fun getCurrentContentTitle(player: Player): String {
        return player.currentTrack()?.title ?: context.getString(R.string.undefined)
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        player.currentTrack()?.cover?.data?.img?.let { source ->
            val cached = lruCache.get(source)
            if (cached != null) {
                return cached
            }
            jobRunner.runAndCancelPrevious {
                scope.launch(IoDispatcher + CoroutineExceptionHandler { throwable ->
                    Logger.e(throwable)
                }) {
                    context.loadImageDrawable(source, imageSize).findBitmap()?.let {
                        lruCache.put(source, it)
                        callback.onBitmap(it)
                    }
                }
            }
        }
        return null
    }

    override fun notificationFlow(): Flow<PlayerNotification> = channel.asFlow()

    override fun onNotificationStarted(notificationId: Int, notification: Notification) {
        Logger.d("onNotificationStarted() called with: notificationId = $notificationId, notification = $notification")
        scope.launch { channel.send(PlayerNotification.Show(notificationId, notification)) }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        Logger.d("onNotificationCancelled() called with: notificationId = $notificationId, dismissedByUser = $dismissedByUser")
        if (lruCache.size() > 0) {
            lruCache.evictAll()
        }
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