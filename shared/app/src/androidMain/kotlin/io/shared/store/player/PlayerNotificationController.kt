package io.shared.store.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Size
import androidx.collection.LruCache
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import io.shared.app.R
import io.shared.core.IoDispatcher
import io.shared.core.Logger
import io.shared.core.extensions.CoroutineExceptionHandler
import io.shared.core.extensions.JobRunner
import io.shared.imageloader.loadImageDrawable
import io.shared.model.TrackItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayerNotificationController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val pendingIntent: PendingIntent
) : PlayerNotificationManager.MediaDescriptionAdapter,
    PlayerNotificationManager.NotificationListener,
    PlayerNotificationPipeline {

    private val stateFlow = MutableStateFlow<PlayerNotification?>(null)

    private val jobRunner = JobRunner()
    private val imageSize = Size(200, 200)
    private var lruCache = LruCache<String, Bitmap>(1)

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return pendingIntent
    }

    override fun getCurrentContentText(player: Player): String? {
        return player.currentTrack?.subTitle
    }

    override fun getCurrentContentTitle(player: Player): String {
        return player.currentTrack?.title ?: context.getString(R.string.undefined)
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        player.currentTrack?.cover?.data?.img?.let { source ->
            val cached = lruCache.get(source)
            if (cached != null) {
                return cached
            }
            jobRunner.runAndCancelPrevious {
                scope.launch(IoDispatcher + CoroutineExceptionHandler { throwable ->
                    Logger.e(
                        message = "Error while loading image drawable in notification for source $source",
                        throwable = throwable
                    )
                }) {
                    context.loadImageDrawable(source, imageSize).getBitmap()?.let {
                        lruCache.put(source, it)
                        callback.onBitmap(it)
                    }
                }
            }
        }
        return null
    }

    override fun notificationFlow(): Flow<PlayerNotification> = stateFlow.filterNotNull()

    override fun onNotificationStarted(notificationId: Int, notification: Notification) {
        Logger.d("onNotificationStarted() called with: notificationId = $notificationId, notification = $notification")
        stateFlow.value = PlayerNotification.Show(notificationId, notification)
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        Logger.d("onNotificationCancelled() called with: notificationId = $notificationId, dismissedByUser = $dismissedByUser")
        if (lruCache.size() > 0) {
            lruCache.evictAll()
        }
        stateFlow.value = PlayerNotification.Cancel(notificationId, dismissedByUser)
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        Logger.d("onNotificationPosted() called with: notificationId = $notificationId, notification = $notification, ongoing = $ongoing")
        stateFlow.value = PlayerNotification.Posted(
            notificationId,
            notification,
            ongoing
        )
    }

    private val Player.currentTrack: TrackItem?
        get() {
            return currentTag as? TrackItem
        }


    private fun Drawable?.getBitmap(): Bitmap? {
        return (this as? BitmapDrawable)?.bitmap
    }
}