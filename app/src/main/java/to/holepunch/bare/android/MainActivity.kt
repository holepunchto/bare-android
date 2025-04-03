package to.holepunch.bare.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import to.holepunch.bare.android.utils.PhoneAccountManager
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  private var worklet: Worklet? = null
  private lateinit var phoneAccountManager: PhoneAccountManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
      && Build.VERSION.SDK_INT > 33) {
      requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
      Log.v("App", "Add POST_NOTIFICATIONS perm")
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

    phoneAccountManager.registerPhoneAccount()
  }

  override fun onDestroy() {
    super.onDestroy()
    worklet!!.terminate()
    worklet = null
  }
}
