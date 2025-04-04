package to.holepunch.bare.android

import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import org.json.JSONObject

import to.holepunch.bare.android.utils.CallManager
import to.holepunch.bare.android.utils.NotificationManagerUtils
import to.holepunch.bare.android.utils.NotificationUtils
import to.holepunch.bare.kit.Worklet
import to.holepunch.bare.kit.MessagingService as BaseMessagingService

class MessagingService : BaseMessagingService(Worklet.Options()) {
  private lateinit var callManager: CallManager

  override fun onCreate() {
    Log.v(TAG, "Create messaging service")
    super.onCreate()

    NotificationManagerUtils.createPushNotificationChannel(applicationContext)
    callManager = CallManager(applicationContext)

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
        putString("CONNECTION_ID", reply.optString("id", "0000000"))
        putString("CALLER_NAME", reply.optString("caller", "unknown"))
      }

      callManager.addNewIncomingCall(extras)
      return
    }

    try {
      val notification = NotificationUtils.getPushNotification(applicationContext, reply.optString("title", "Default title"), reply.optString("body", "Default description"))
      NotificationManagerUtils.getManager(applicationContext).notify(
        1,
        notification
      )
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onNewToken(token: String) {
    Log.v("MessagingService", "Token: $token")
  }

  companion object {
    private const val TAG = "MessagingService"
  }
}
