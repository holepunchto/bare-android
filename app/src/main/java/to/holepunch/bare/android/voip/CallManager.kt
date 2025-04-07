package to.holepunch.bare.android.voip

import android.content.Context
import android.telecom.TelecomManager

class CallManager(private val context: Context) {
  private val telecom: TelecomManager by lazy {
    context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
  }

  private val phoneAccountManager = PhoneAccountManager(context)

  fun addNewIncomingCall(extras: android.os.Bundle) {
    telecom.addNewIncomingCall(phoneAccountManager.getPhoneAccountHandle(), extras)
  }
}
