package to.holepunch.bare.android.voip

import android.app.NotificationManager
import android.content.Context
import android.telecom.ConnectionRequest
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.Connection as BaseConnection
import android.telecom.ConnectionService as BaseConnectionService

class ConnectionService : BaseConnectionService() {
  companion object {
    val connections: MutableMap<String, Connection> = mutableMapOf()

    fun startCall(id: String) {
      val conn = connections[id]

      conn?.setActive()
    }

    fun declineCall(context: Context, id: String) {
      val conn = connections[id]

      conn?.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
      conn?.destroy()

      connections.remove(id)

      val notificationManager = context.getSystemService(NotificationManager::class.java)

      notificationManager.cancel(10)
    }
  }

  override fun onCreateIncomingConnection(
    connectionManagerPhoneAccount: PhoneAccountHandle?,
    request: ConnectionRequest?
  ): Connection {
    val id = request?.extras?.getString("CONNECTION_ID")!!
    val caller = request.extras?.getString("CALLER_NAME")!!

    val conn = Connection(applicationContext, caller, id)

    conn.setConnectionProperties(BaseConnection.PROPERTY_SELF_MANAGED)
    conn.setAudioModeIsVoip(true)

    connections[id] = conn

    return conn
  }
}
