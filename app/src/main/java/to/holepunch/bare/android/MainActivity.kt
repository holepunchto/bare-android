package to.holepunch.bare.android

import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  private var worklet: Worklet? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Log.v("App", "2")

    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.MANAGE_OWN_CALLS), 0)
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
  }

  override fun onDestroy() {
    super.onDestroy()

    worklet!!.terminate()
    worklet = null
  }
}
