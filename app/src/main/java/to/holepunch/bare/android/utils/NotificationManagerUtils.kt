package to.holepunch.bare.android.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class NotificationManagerUtils {
    companion object {
        const val INCOMING_CALL_CHANNEL = "incoming_call"
        const val PUSH_NOTIFICATION_CHANNEL = "custom_channel_id"

        fun createIncomingCallChannel(ctx: Context) {
            val notificationManager: NotificationManager = getManager(ctx);
            val channel = NotificationChannel(
                INCOMING_CALL_CHANNEL, "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        fun notifyIncomingCallChannel(ctx: Context, notification: Notification, id: Int) {
            val notificationManager: NotificationManager = getManager(ctx);
            notificationManager.notify(INCOMING_CALL_CHANNEL, id, notification)
        }

        fun cancelIncomingCallChannel(ctx: Context, id: Int) {
            val notificationManager: NotificationManager = getManager(ctx);
            notificationManager.cancel(id)
        }

        fun notifyPushNotificationChannel(ctx: Context, notification: Notification, id: Int) {
            val notificationManager: NotificationManager = getManager(ctx);
            notificationManager.notify(PUSH_NOTIFICATION_CHANNEL, id, notification)
        }

        fun createPushNotificationChannel(ctx: Context) {
            val notificationManager: NotificationManager = getManager(ctx);
            val channel = NotificationChannel(
                PUSH_NOTIFICATION_CHANNEL,
                "Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        private fun getManager(ctx: Context): NotificationManager {
            return ctx.getSystemService(
            NotificationManager::class.java
            )
        }
    }
}