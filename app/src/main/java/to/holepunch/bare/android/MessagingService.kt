package to.holepunch.bare.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import org.json.JSONObject
import to.holepunch.bare.android.voip.VoIPManager
import to.holepunch.bare.kit.Worklet
import to.holepunch.bare.kit.MessagingService as BaseMessagingService

class MessagingService : BaseMessagingService(Worklet.Options()) {
  private val notificationManager: NotificationManager by lazy {
    getSystemService(NotificationManager::class.java)
  }

  private val voipManager: VoIPManager by lazy {
    VoIPManager(this)
  }

  override fun onCreate() {
    super.onCreate()

    notificationManager.createNotificationChannel(
      NotificationChannel(
        PUSH_NOTIFICATION_CHANNEL,
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
    when (reply.optString("type")) {
      "call" -> {
        voipManager.addNewIncomingCall(
          reply.optString("id", "1234567"),
          reply.optString("caller", "Unknown")
        )
      }

      "notification" -> {
        try {
          notificationManager.notify(
            1,
            Notification.Builder(this, PUSH_NOTIFICATION_CHANNEL)
              .setSmallIcon(android.R.drawable.ic_dialog_info)
              .setContentTitle(reply.optString("title", "Default title"))
              .setContentText(reply.optString("body", "Default description"))
              .setAutoCancel(true)
              .build()
          )
        } catch (e: Exception) {
          throw RuntimeException(e)
        }
      }
    }
  }

  override fun onNewToken(token: String) {
    Log.v(TAG, "Token: $token")
  }

  companion object {
    private const val TAG = "MessagingService"
    private const val PUSH_NOTIFICATION_CHANNEL = "push_notification"
  }
}
