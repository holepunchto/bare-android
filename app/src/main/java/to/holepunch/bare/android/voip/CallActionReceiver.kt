package to.holepunch.bare.android.voip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CallActionReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val id = intent.getStringExtra("CONNECTION_ID") ?: return

    when (intent.action) {
      "ANSWER_CALL" -> {
        ConnectionService.startCall(id)
      }

      "DECLINE_CALL" -> {
        ConnectionService.declineCall(context, id)
      }
    }
  }
}
