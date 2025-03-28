package to.holepunch.bare.android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  private var worklet: Worklet? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Log.v("App", "14")

    val permission = mutableListOf<String>()

    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
      && Build.VERSION.SDK_INT > 33) {
      permission.add(Manifest.permission.POST_NOTIFICATIONS)
      Log.v("App", "Add POST_NOTIFICATIONS perm")
    }

    if (checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
      permission.add(Manifest.permission.READ_PHONE_NUMBERS)
      Log.v("App", "Add POST_NOTIFICATIONS perm")
    }

    if (permission.isNotEmpty()) {
      requestPermissions(permission.toTypedArray(), 0)
      Log.v("App", "Permissions requested")
    }

    worklet = Worklet(null)

    try {
      worklet!!.start("/app.bundle", assets.open("app.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onPause() {
    super.onPause()

    worklet!!.suspend()
  }

  override fun onResume() {
    super.onResume()

    worklet!!.resume()

    Log.v("App", "onResume")
    val tm = applicationContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    try {
      val phoneAccount = PhoneAccount.builder(getPhoneAccountHandle(), "Bare Android")
        .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
        .build()
      tm.registerPhoneAccount(phoneAccount)
    } catch (e: Exception) {
      Log.v("App", "Not able to register", e)
    }

    val isEnabled = try {
      tm.getPhoneAccount(getPhoneAccountHandle())?.isEnabled == true
    } catch (e: SecurityException) {
      Log.w("VoIP", "Missing READ_PHONE_NUMBERS permission", e)
      false
    }

    if (!isEnabled) {
      Handler(Looper.getMainLooper()).post {
        if (!isFinishing && !isDestroyed) {
          promptToEnablePhoneAccount(this)
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    worklet!!.terminate()
    worklet = null
  }

  private fun promptToEnablePhoneAccount(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Enable Call Feature")
    builder.setMessage("To receive calls, Bare needs permission to manage calls. Do you want to enable this in settings?")

    builder.setPositiveButton("Yes") { dialog, _ ->
      dialog.dismiss()
      val intent = Intent()
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
      intent.setComponent(ComponentName("com.android.server.telecom",
        "com.android.server.telecom.settings.EnableAccountPreferenceActivity"))
      context.startActivity(intent)
    }

    builder.setNegativeButton("No") { dialog, _ ->
      dialog.dismiss()
    }

    builder.show()
  }

  private fun getPhoneAccountHandle(): PhoneAccountHandle {
    return PhoneAccountHandle(
      ComponentName(applicationContext, MyConnectionService::class.java),
      applicationContext.packageName
    )
  }
}
