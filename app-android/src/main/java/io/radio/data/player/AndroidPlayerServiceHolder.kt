package io.radio.data.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import io.radio.di.Qualifier.PlayerCoroutineQualifier
import io.shared.core.MainDispatcher
import io.shared.store.player.MediaPlayer
import io.shared.store.player.PlayerNotification
import io.shared.store.player.PlayerNotificationController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.di
import org.kodein.di.instance

class AndroidPlayerServiceHolder : Service(), DIAware {

    override val di: DI by di()

    private val playerScope: CoroutineScope by instance(PlayerCoroutineQualifier)
    private val playerController: MediaPlayer by instance()
    private val notificationController by instance<PlayerNotificationController>()

    override fun onCreate() {
        super.onCreate()
        notificationController.notificationFlow()
            .onEach {
                withContext(MainDispatcher) {
                    when (it) {
                        is PlayerNotification.Posted -> {
                            if (it.ongoing) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    startForeground(
                                        it.id,
                                        it.notification,
                                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                                    )
                                } else {
                                    startForeground(it.id, it.notification)
                                }
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
        playerScope.coroutineContext.cancelChildren()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun initialize(context: Context) {
            val intent = Intent(context, AndroidPlayerServiceHolder::class.java)
            context.startService(intent)
        }
    }
}