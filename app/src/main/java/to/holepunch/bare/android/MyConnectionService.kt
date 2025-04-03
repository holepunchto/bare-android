package to.holepunch.bare.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.app.NotificationCompat

class MyConnection(private val ctx: Context, private val recipientName: String, public val id: String) : Connection() {
    companion object {
        private const val TAG = "MyConnection"
        private const val YOUR_CHANNEL_ID = "custom_channel_id"
    }

    // These will be raised if the user answers your call via a Bluetooth device or another device
    // like a wearable or automotive calling UX.
    override fun onAnswer() {
        setActive()
        Log.v(TAG, "onAnswer")
    }

    // Handle requests to reject the call which are raised via Bluetooth or other calling surfaces.
    override fun onReject() {
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
        Log.v(TAG, "onReject")
    }

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.v(TAG, "onShowIncomingCallUi")

        val notificationManager: NotificationManager = ctx.getSystemService(
            NotificationManager::class.java
        )
        val channel = NotificationChannel(
            YOUR_CHANNEL_ID, "Incoming Calls",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val answerIntent = Intent(ctx, CallActionReceiver::class.java).apply {
            action = "ANSWER_CALL"
            putExtra("CONNECTION_ID", id)
        }
        val answerPendingIntent = PendingIntent.getBroadcast(ctx, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)

        val declineIntent = Intent(ctx, CallActionReceiver::class.java).apply {
            action = "DECLINE_CALL"
            putExtra("CONNECTION_ID", id)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(ctx, 1, declineIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(ctx, YOUR_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Incoming call from $recipientName")
            .setContentText("Voice Call")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_call, "Answer", answerPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declinePendingIntent)
            .build()

        notification.flags = notification.flags or Notification.FLAG_INSISTENT
        notificationManager.notify(YOUR_CHANNEL_ID, 1, notification)

        Log.v(TAG, "onShowIncomingCallUi end")
    }
}

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.v("CallActionReceive", "in onReceive")
        val connectionId = intent.getStringExtra("CONNECTION_ID") ?: return
        Log.v("CallActionReceive", "get connection id")

        when (intent.action) {
            "ANSWER_CALL" -> {
                Log.v("CallActionReceive", "ANSWER_CALL")
                MyConnectionService.startCall(connectionId)
            }
            "DECLINE_CALL" -> {
                Log.v("CallActionReceive", "DECLINE_CALL")
                MyConnectionService.declineCall(connectionId)
            }
        }
    }
}

class MyConnectionService: ConnectionService() {
    companion object {
        val activeConnections: MutableMap<String, MyConnection> = mutableMapOf()

        fun startCall(id: String) {
            val con = this.getConnection(id)
            Log.v(TAG, "found connection for id ${con?.id}")
            con?.setActive()

            // Later
            con?.setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
            con?.destroy()
            this.removeConnection(id)
        }

        fun declineCall(id: String) {
            val con = this.getConnection(id)
            Log.v(TAG, "found connection for id ${con?.id}")
            con?.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            con?.destroy()
            this.removeConnection(id)
        }

        private fun getConnection(connectionId: String): MyConnection? {
            return activeConnections[connectionId]
        }

        private fun removeConnection(connectionId: String) {
            activeConnections.remove(connectionId)
        }

        private const val TAG = "MyConnectionService"
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.v(TAG, "In on createIncomingConnection")
        val connectionId = "some_unique_id" // Use the phone number or a UUID
        val conn = MyConnection(applicationContext, "tony", connectionId)

        conn.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        conn.setCallerDisplayName("bare dev", TelecomManager.PRESENTATION_ALLOWED)
        conn.setAudioModeIsVoip(true)

        activeConnections[connectionId] = conn
        return conn
    }
}