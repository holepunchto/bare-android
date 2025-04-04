package to.holepunch.bare.android.utils

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

        fun createPushNotificationChannel(ctx: Context) {
            val notificationManager: NotificationManager = getManager(ctx);
            val channel = NotificationChannel(
                PUSH_NOTIFICATION_CHANNEL,
                "Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        fun getManager(ctx: Context): NotificationManager {
            return ctx.getSystemService(
            NotificationManager::class.java
            )
        }
    }
}