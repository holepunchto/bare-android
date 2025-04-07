package to.holepunch.bare.android.voip

import android.content.Context
import android.telecom.TelecomManager

class CallManager(private val context: Context) {
  private val telecomManager: TelecomManager by lazy {
    context.getSystemService(TelecomManager::class.java)
  }

  private val phoneAccountManager = PhoneAccountManager(context)

  fun addNewIncomingCall(extras: android.os.Bundle) {
    telecomManager.addNewIncomingCall(phoneAccountManager.getPhoneAccountHandle(), extras)
  }
}
