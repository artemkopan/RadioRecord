package io.radio.data.player

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationMediaDescriptionAdapter(
    private val context: Context,
    private val scope: CoroutineScope
) : PlayerNotificationManager.MediaDescriptionAdapter {

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
}