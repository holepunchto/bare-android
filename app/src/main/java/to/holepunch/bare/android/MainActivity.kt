package to.holepunch.bare.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import to.holepunch.bare.android.voip.PhoneAccountManager
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  private val phoneAccountManager: PhoneAccountManager by lazy {
    PhoneAccountManager(this)
  }

  private lateinit var worklet: Worklet

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }

    worklet = Worklet(null)

    try {
      worklet.start("/app.bundle", assets.open("app.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onPause() {
    super.onPause()

    worklet.suspend()
  }

  override fun onResume() {
    super.onResume()

    worklet.resume()

    phoneAccountManager.registerPhoneAccount()
  }

  override fun onDestroy() {
    super.onDestroy()

    worklet.terminate()
  }
}
