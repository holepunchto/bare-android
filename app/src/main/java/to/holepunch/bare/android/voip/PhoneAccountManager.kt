package to.holepunch.bare.android.voip

import android.content.ComponentName
import android.content.Context
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager

class PhoneAccountManager(private val context: Context) {
  private val telecomManager: TelecomManager by lazy {
    context.getSystemService(TelecomManager::class.java)
  }

  fun getPhoneAccountHandle(): PhoneAccountHandle {
    return PhoneAccountHandle(
      ComponentName(context, ConnectionService::class.java),
      context.packageName
    )
  }

  fun registerPhoneAccount() {
    try {
      val phoneAccount = PhoneAccount
        .builder(getPhoneAccountHandle(), "Bare Android")
        .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
        .build()

      telecomManager.registerPhoneAccount(phoneAccount)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}
