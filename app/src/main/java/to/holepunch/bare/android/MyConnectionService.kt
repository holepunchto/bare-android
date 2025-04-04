package to.holepunch.bare.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi

import to.holepunch.bare.android.utils.NotificationManagerUtils
import to.holepunch.bare.android.utils.NotificationUtils

class MyConnection(private val ctx: Context, private val recipientName: String, val id: String) : Connection() {
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()

        NotificationManagerUtils.createIncomingCallChannel(ctx)

        val notificationManager = NotificationManagerUtils.getManager(ctx)
        val notification = NotificationUtils.getIncomingCallNotification(ctx, recipientName, id)
        notificationManager.notify(YOUR_CHANNEL_ID, 1, notification)
    }
}

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectionId = intent.getStringExtra("CONNECTION_ID") ?: return

        when (intent.action) {
            "ANSWER_CALL" -> {
                MyConnectionService.startCall(connectionId)
            }
            "DECLINE_CALL" -> {
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
        val connectionId = request?.extras?.getString("CONNECTION_ID") ?: "default_connection_id"
        val callerName = request?.extras?.getString("CALLER_NAME") ?: "Unknown"
        val conn = MyConnection(applicationContext, callerName, connectionId)

        conn.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        conn.setCallerDisplayName("bare dev", TelecomManager.PRESENTATION_ALLOWED)
        conn.setAudioModeIsVoip(true)

        activeConnections[connectionId] = conn
        return conn
    }
}