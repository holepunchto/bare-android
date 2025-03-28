package to.holepunch.bare.android

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import to.holepunch.bare.android.utils.PhoneAccountManager
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  private var worklet: Worklet? = null
  private lateinit var phoneAccountManager: PhoneAccountManager

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

    phoneAccountManager = PhoneAccountManager(applicationContext)
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

    phoneAccountManager.registerPhoneAccount()

    if (!phoneAccountManager.isPhoneAccountEnabled()) {
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
      startActivity(phoneAccountManager.getEnablePhoneAccountIntent())
    }

    builder.setNegativeButton("No") { dialog, _ ->
      dialog.dismiss()
    }

    builder.show()
  }
}
