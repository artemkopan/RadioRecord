package io.radio.data.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import io.radio.shared.base.MainDispatcher
import io.radio.shared.store.player.MediaPlayer
import io.radio.shared.store.player.notifications.PlayerNotification
import io.radio.shared.store.player.notifications.PlayerNotificationController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class AndroidPlayerServiceHolder : Service() {

    private val playerScope = CoroutineScope(SupervisorJob())
    private val playerController by inject<MediaPlayer>()
    private val notificationController by inject<PlayerNotificationController>()

    override fun onCreate() {
        super.onCreate()
        notificationController.notificationFlow()
            .onEach {
                withContext(MainDispatcher) {
                    when (it) {
                        is PlayerNotification.Posted -> {
                            if (it.ongoing) {
                                startForeground(it.id, it.notification)
                            } else {
                                stopForeground(false)
                            }
                        }
                    }
                }
            }
            .launchIn(playerScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch { playerController.release() }
        playerScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun initialize(context: Context) {
            val intent = Intent(context, AndroidPlayerServiceHolder::class.java)
            context.startService(intent)
        }
    }
}