package to.holepunch.bare.android

import android.R.drawable
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import org.json.JSONObject
import to.holepunch.bare.kit.Worklet
import to.holepunch.bare.kit.MessagingService as BaseMessagingService

class MessagingService : BaseMessagingService(Worklet.Options()) {
  private var notificationManager: NotificationManager? = null

  override fun onCreate() {
    super.onCreate()

    notificationManager = getSystemService(NotificationManager::class.java)

    notificationManager!!.createNotificationChannel(
      NotificationChannel(
        CHANNEL_ID,
        "Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
      )
    )

    try {
      this.start("/push.bundle", assets.open("push.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onWorkletReply(reply: JSONObject) {
    try {
      notificationManager!!.notify(
        1,
        Notification.Builder(this, CHANNEL_ID)
          .setSmallIcon(drawable.ic_dialog_info)
          .setContentTitle(reply.optString("title", "Default title"))
          .setContentText(reply.optString("body", "Default description"))
          .setAutoCancel(true)
          .build()
      )
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onNewToken(token: String) {
    Log.v("MessagingService", "Token: $token")
  }

  companion object {
    private const val CHANNEL_ID = "custom_channel_id"
  }
}
