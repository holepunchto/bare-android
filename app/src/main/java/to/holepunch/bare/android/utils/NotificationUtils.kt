package to.holepunch.bare.android.utils

import android.R
import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import to.holepunch.bare.android.CallActionReceiver

class BlankCallActivity : Activity() {
    companion object {
        const val TAG = "IncomingCallActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "IncomingCallActivity launched successfully")

        // This is where you can setup your UI for answering/declining the call
        // For now, we will just log that it's working
    }
}

class NotificationUtils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        fun getIncomingCallNotification(ctx: Context, recipientName: String, connectionId: String): Notification {
            val answerIntent = Intent(ctx, CallActionReceiver::class.java).apply {
                action = "ANSWER_CALL"
                putExtra("CONNECTION_ID", connectionId)
            }
            val answerPendingIntent = PendingIntent.getBroadcast(ctx, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)

            val declineIntent = Intent(ctx, CallActionReceiver::class.java).apply {
                action = "DECLINE_CALL"
                putExtra("CONNECTION_ID", connectionId)
            }
            val declinePendingIntent = PendingIntent.getBroadcast(ctx, 1, declineIntent, PendingIntent.FLAG_IMMUTABLE)

            val fullScreenIntent = Intent(ctx, BlankCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val incomingCaller = Person.Builder()
                .setName(recipientName)
                .setImportant(true)
                .build()
            val builder = Notification.Builder(ctx, NotificationManagerUtils.INCOMING_CALL_CHANNEL)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .setStyle(
                    Notification.CallStyle.forIncomingCall(incomingCaller, declinePendingIntent, answerPendingIntent))
                .addPerson(incomingCaller)

            return builder.build()
        }

        fun getPushNotification(ctx: Context, title: String, body: String): Notification {
            return Notification.Builder(ctx, NotificationManagerUtils.PUSH_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()
        }
    }
}