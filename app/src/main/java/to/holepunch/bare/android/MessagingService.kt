package to.holepunch.bare.android

import android.R.drawable
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import org.json.JSONObject
import to.holepunch.bare.kit.Worklet
import to.holepunch.bare.kit.MessagingService as BaseMessagingService

class MessagingService : BaseMessagingService(Worklet.Options()) {
  private var notificationManager: NotificationManager? = null
  private var telecomManager: TelecomManager? = null

  override fun onCreate() {
    Log.v(TAG, "Create messaging service")
    super.onCreate()

    notificationManager = getSystemService(NotificationManager::class.java)
    notificationManager!!.createNotificationChannel(
      NotificationChannel(
        CHANNEL_ID,
        "Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
      )
    )

    telecomManager = applicationContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    try {
      this.start("/push.bundle", assets.open("push.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onWorkletReply(reply: JSONObject) {
    Log.v(TAG, "onWorkletReply")
    if (reply.optString("type") == "call") {



      val extras = Bundle().apply {
        putParcelable(
          TelecomManager.EXTRA_INCOMING_CALL_ADDRESS,
          Uri.fromParts("user", reply.optString("caller", "unknown caller"), null)
        )
      }

      telecomManager!!.addNewIncomingCall(getPhoneAccountHandle(), extras)

      return
    }

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

  private fun getPhoneAccountHandle(): PhoneAccountHandle {
    return PhoneAccountHandle(
      ComponentName(applicationContext, MyConnectionService::class.java),
      applicationContext.packageName
    )
  }

  companion object {
    private const val CHANNEL_ID = "custom_channel_id"
    private const val TAG = "MessagingService"
  }
}
