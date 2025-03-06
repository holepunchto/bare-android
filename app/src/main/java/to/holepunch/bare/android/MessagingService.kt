package to.holepunch.bare.android

import android.R.drawable
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import to.holepunch.bare.kit.MessagingService as BaseMessagingService
import to.holepunch.bare.kit.Worklet
import java.io.IOException

class MessagingService : BaseMessagingService(Worklet.Options()) {
  private var notificationManager: NotificationManager? = null

  init {
    Log.v("CustomMessagingService", "Worklet created!")
  }

  override fun onCreate() {
    super.onCreate()

    notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager!!.createNotificationChannel(
      NotificationChannel(
        CHANNEL_ID,
        "Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
      )
    )

    val context = applicationContext
    val assetManager = context.assets

    try {
      this.start("app.bundle", assetManager.open("push.bundle"), null)
      Log.v("CustomMessagingService", "Worklet started!")
    } catch (e: IOException) {
      Log.e("CustomMessagingService", "Failed to start worklet", e)
    }
  }

  override fun onWorkletReply(reply: JSONObject) {
    Log.v("CustomMessagingService", "json: $reply")
    try {
      notificationManager!!.notify(
        1, NotificationCompat.Builder(this, CHANNEL_ID)
          .setSmallIcon(drawable.ic_dialog_info)
          .setContentTitle(reply.optString("title", "Default title"))
          .setContentText(reply.optString("body", "Default description"))
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          .setAutoCancel(true)
          .build()
      )
    } catch (e: Exception) {
      Log.e("CustomMessagingService", "Error showing notification", e)
    }
  }

  override fun onNewToken(token: String) {
    Log.v("CustomMessagingService", "token: $token")
  }

  companion object {
    private const val CHANNEL_ID = "custom_channel_id"
  }
}
