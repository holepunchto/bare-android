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
import android.util.Log
import androidx.annotation.RequiresApi

import to.holepunch.bare.android.utils.NotificationManagerUtils
import to.holepunch.bare.android.utils.NotificationUtils

class MyConnection(private val ctx: Context, private val recipientName: String, val id: String) : Connection() {
    companion object {
        private const val TAG = "MyConnection"
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

        val notification = NotificationUtils.getIncomingCallNotification(ctx, recipientName, id)
        NotificationManagerUtils.notifyIncomingCallChannel(ctx,  notification, 10)
    }
}

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectionId = intent.getStringExtra("CONNECTION_ID") ?: return
        Log.v("CallActionReceiver", "connection id $connectionId")

        when (intent.action) {
            "ANSWER_CALL" -> {
                MyConnectionService.startCall(connectionId)
            }
            "DECLINE_CALL" -> {
                MyConnectionService.declineCall(context, connectionId)
            }
        }
    }
}

class MyConnectionService: ConnectionService() {
    companion object {
        val activeConnections: MutableMap<String, MyConnection> = mutableMapOf()

        fun startCall(id: String) {
            val con = activeConnections[id]
            Log.v(TAG, "found connection for id ${con?.id}")
            con?.setActive()
        }

        fun declineCall(ctx: Context, id: String) {
            val con = activeConnections[id]
            Log.v(TAG, "found connection for id ${con?.id}")
            con?.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            con?.destroy()
            activeConnections.remove(id)
            NotificationManagerUtils.cancelIncomingCallChannel(ctx, 10)
        }

        private const val TAG = "MyConnectionService"
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connectionId = request?.extras?.getString("CONNECTION_ID") ?: "some_id"
        val callerName = request?.extras?.getString("CALLER_NAME") ?: "Unknown"
        Log.v("ConnectionService", "connection id: $connectionId")
        Log.v("ConnectionService", "callerName: $callerName")
        val conn = MyConnection(applicationContext, callerName, connectionId)

        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        conn.setAudioModeIsVoip(true)

        activeConnections[connectionId] = conn
        return conn
    }
}