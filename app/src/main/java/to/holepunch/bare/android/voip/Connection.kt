package to.holepunch.bare.android.voip

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.telecom.DisconnectCause
import android.telecom.Connection as BaseConnection

class Connection(
  private val context: Context,
  private val id: String,
  private val caller: String
) : BaseConnection() {
  private val notificationManager: NotificationManager by lazy {
    context.getSystemService(NotificationManager::class.java)
  }

  override fun onAnswer() {
    setActive()
  }

  override fun onReject() {
    setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
    destroy()
  }

  override fun onShowIncomingCallUi() {
    super.onShowIncomingCallUi()

    notificationManager.createNotificationChannel(
      NotificationChannel(
        INCOMING_CALL_CHANNEL,
        "Incoming Calls",
        NotificationManager.IMPORTANCE_HIGH
      )
    )

    val answerIntent = Intent(context, CallActionReceiver::class.java).apply {
      action = "ANSWER_CALL"
      putExtra("CONNECTION_ID", id)
    }

    val answerPendingIntent =
      PendingIntent.getBroadcast(context, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)

    val declineIntent = Intent(context, CallActionReceiver::class.java).apply {
      action = "DECLINE_CALL"
      putExtra("CONNECTION_ID", id)
    }

    val declinePendingIntent =
      PendingIntent.getBroadcast(context, 1, declineIntent, PendingIntent.FLAG_IMMUTABLE)

    val fullScreenIntent = Intent(context, CallActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
    }

    val fullScreenPendingIntent = PendingIntent.getActivity(
      context,
      0,
      fullScreenIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val person = Person.Builder()
      .setName(caller)
      .setImportant(true)
      .build()

    notificationManager.notify(
      10,
      Notification.Builder(context, INCOMING_CALL_CHANNEL)
        .setFullScreenIntent(fullScreenPendingIntent, true)
        .setSmallIcon(android.R.drawable.sym_call_incoming)
        .setStyle(
          Notification.CallStyle.forIncomingCall(
            person,
            declinePendingIntent,
            answerPendingIntent
          )
        )
        .addPerson(person)
        .build()
    )
  }

  companion object {
    private const val INCOMING_CALL_CHANNEL = "incoming_call"
  }
}
