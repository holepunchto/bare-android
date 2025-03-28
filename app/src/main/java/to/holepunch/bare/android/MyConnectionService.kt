package to.holepunch.bare.android

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log

class MyConnection : Connection() {
    companion object {
        private const val TAG = "MyConnection"
    }

    override fun onAnswer() {
        setActive()
        Log.v(TAG, "onAnswer")
    }

    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
        Log.v(TAG, "onDisconnect")
    }

    override fun onReject() {
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
        Log.v(TAG, "onReject")
    }
}

class MyConnectionService: ConnectionService() {
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val conn = MyConnection()
        conn.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        conn.setDialing()
        return conn
    }
}