package io.shared.store.player

import android.app.Notification


sealed class PlayerNotification {

    data class Show(val id: Int, val notification: Notification) : PlayerNotification()
    data class Cancel(val id: Int, val dismissedByUser: Boolean) : PlayerNotification()
    data class Posted(val id: Int, val notification: Notification, val ongoing: Boolean) :
        PlayerNotification()
}