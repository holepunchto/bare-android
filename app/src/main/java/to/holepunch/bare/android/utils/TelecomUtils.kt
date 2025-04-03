package to.holepunch.bare.android.utils

import android.content.ComponentName
import android.content.Context
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import to.holepunch.bare.android.MyConnectionService

class PhoneAccountManager(private val context: Context) {
    private val telecomManager: TelecomManager by lazy {
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    fun getPhoneAccountHandle(): PhoneAccountHandle {
        return PhoneAccountHandle(
            ComponentName(context, MyConnectionService::class.java),
            context.packageName
        )
    }

    fun registerPhoneAccount() {
        try {
            val phoneAccount = PhoneAccount.builder(getPhoneAccountHandle(), "Bare Android")
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
                .build()
            telecomManager.registerPhoneAccount(phoneAccount)
        } catch (e: Exception) {
            Log.v("PhoneAccountManager", "Not able to register", e)
        }
    }
}

class CallManager(private val context: Context) {
    private val telecomManager: TelecomManager by lazy {
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }
    private val phoneAccountManager = PhoneAccountManager(context)

    fun addNewIncomingCall(extras: android.os.Bundle) {
        telecomManager.addNewIncomingCall(phoneAccountManager.getPhoneAccountHandle(), extras)
    }
} 