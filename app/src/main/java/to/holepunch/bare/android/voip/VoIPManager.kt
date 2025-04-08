package to.holepunch.bare.android.voip

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager

class VoIPManager(private val context: Context) {
  private val telecomManager: TelecomManager by lazy {
    context.getSystemService(TelecomManager::class.java)
  }

  fun getPhoneAccountHandle(): PhoneAccountHandle {
    return PhoneAccountHandle(
      ComponentName(context, ConnectionService::class.java),
      context.packageName
    )
  }

  fun addNewIncomingCall(id: String, caller: String) {
    val extras = Bundle().apply {
      putParcelable(
        TelecomManager.EXTRA_INCOMING_CALL_ADDRESS,
        Uri.fromParts("user", caller, null)
      )
      putString("CONNECTION_ID", id)
      putString("CALLER_NAME", caller)
    }

    telecomManager.addNewIncomingCall(getPhoneAccountHandle(), extras)
  }

  fun registerPhoneAccount(label: String) {
    try {
      val phoneAccount = PhoneAccount
        .builder(getPhoneAccountHandle(), label)
        .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
        .build()

      telecomManager.registerPhoneAccount(phoneAccount)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}
